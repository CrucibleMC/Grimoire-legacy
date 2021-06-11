package io.github.crucible.omniconfig.wrappers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import io.github.crucible.omniconfig.OmniconfigCore;
import io.github.crucible.omniconfig.core.Configuration;
import io.github.crucible.omniconfig.core.SynchronizationManager;
import io.github.crucible.omniconfig.core.Configuration.SidedConfigType;
import io.github.crucible.omniconfig.core.Configuration.VersioningPolicy;
import io.github.crucible.omniconfig.lib.Perhaps;
import io.github.crucible.omniconfig.wrappers.values.AbstractParameter;
import io.github.crucible.omniconfig.wrappers.values.BooleanParameter;
import io.github.crucible.omniconfig.wrappers.values.DoubleParameter;
import io.github.crucible.omniconfig.wrappers.values.EnumParameter;
import io.github.crucible.omniconfig.wrappers.values.IntegerParameter;
import io.github.crucible.omniconfig.wrappers.values.PerhapsParameter;
import io.github.crucible.omniconfig.wrappers.values.StringArrayParameter;
import io.github.crucible.omniconfig.wrappers.values.StringParameter;

public class Omniconfig {
    protected final Configuration config;
    protected final String fileID;
    protected final ImmutableMap<String, AbstractParameter<?>> propertyMap;
    protected final ImmutableList<Consumer<Omniconfig>> updateListeners;
    protected final boolean reloadable;

    protected Omniconfig(Builder builder) {
        this.config = builder.config;
        this.reloadable = builder.reloadable;
        this.fileID = builder.fileID;
        this.propertyMap = builder.propertyMap.build();
        this.updateListeners = builder.updateListeners.build();

        this.config.save();

        if (this.reloadable || this.updateListeners.size() > 0) {
            this.config.attachBeholder();
            this.config.attachReloadingAction(this::onConfigReload);
        }

        OmniconfigRegistry.INSTANCE.registerConfig(this);
    }

    public Collection<AbstractParameter<?>> getLoadedParameters() {
        return this.propertyMap.values();
    }

    public Optional<AbstractParameter<?>> getParameter(String parameterID) {
        return Optional.ofNullable(this.propertyMap.get(parameterID));
    }

    public void forceReload() {
        this.config.load();
        this.updateListeners.forEach(listener -> listener.accept(this));
    }

    public boolean isReloadable() {
        return this.reloadable;
    }

    public File getFile() {
        return this.config.getConfigFile();
    }

    public String getFileName() {
        return this.getFile().getName();
    }

    public String getFileID() {
        return this.fileID;
    }

    public String getVersion() {
        return this.config.getLoadedConfigVersion();
    }

    public SidedConfigType getSidedType() {
        return this.config.getSidedType();
    }

    // Internal methods that should never be exposed via API

    public Configuration getBackingConfig() {
        return this.config;
    }

    protected void onConfigReload(Configuration config) {
        if (this.reloadable) {
            config.load();

            this.propertyMap.entrySet().forEach(entry -> {
                AbstractParameter<?> param = entry.getValue();

                if (!OmniconfigCore.onRemoteServer || !param.isSynchronized()) {
                    param.reloadFrom(this);
                }
            });
        }

        this.updateListeners.forEach(listener -> listener.accept(this));
    }

    // Builder starting methods

    public static Builder builder(String fileName) {
        return builder(fileName, "1.0.0");
    }

    public static Builder builder(String fileName, String version) {
        return builder(fileName, version, false);
    }

    public static Builder builder(String fileName, String version, boolean caseSensitive) {
        return builder(fileName, version, caseSensitive, SidedConfigType.COMMON);
    }

    public static Builder builder(String fileName, String version, boolean caseSensitive, SidedConfigType sidedType) {
        try {
            File file = new File(OmniconfigCore.CONFIG_DIR, fileName+".omniconf");
            String filePath = file.getCanonicalPath();
            String configDirPath = OmniconfigCore.CONFIG_DIR.getCanonicalPath();

            if (!filePath.startsWith(configDirPath))
                throw new IOException("Requested config file [" + filePath + "] resides outside of default configuration directory ["
                        + configDirPath + "]. This is strictly forbidden.");

            String fileID = filePath.replace(configDirPath + OmniconfigCore.FILE_SEPARATOR, "");

            return new Builder(fileID, new Configuration(new File(OmniconfigCore.CONFIG_DIR, fileName+".omniconf"), version, caseSensitive), sidedType);
        } catch (Exception ex) {
            throw new RuntimeException("Something screwed up when loading config!", ex);
        }
    }

    // Builder class

    public static class Builder {
        protected final Configuration config;
        protected String currentCategory = Configuration.CATEGORY_GENERAL;
        protected String prefix = "";
        protected boolean sync = false;
        protected boolean reloadable = false;
        protected final String fileID;
        protected final ImmutableMap.Builder<String, AbstractParameter<?>> propertyMap = ImmutableMap.builder();
        protected final ImmutableList.Builder<Consumer<Omniconfig>> updateListeners = ImmutableList.builder();
        protected final List<AbstractParameter.Builder<?, ?>> incompleteBuilders = new ArrayList<>();

        protected Builder(String fileID, Configuration config, SidedConfigType sidedType) {
            this.config = config;
            this.fileID = fileID;

            this.config.setSidedType(sidedType);
        }

        public Builder synchronize(boolean sync) {
            this.sync = sync;
            return this;
        }

        public Builder versioningPolicy(VersioningPolicy policy) {
            this.config.setVersioningPolicy(policy);
            return this;
        }

        public Builder terminateNonInvokedKeys(boolean terminate) {
            this.config.setTerminateNonInvokedKeys(terminate);
            return this;
        }

        public Builder prefix(String prefix) {
            this.prefix = prefix;
            return this;
        }

        public Builder resetPrefix() {
            this.prefix = "";
            return this;
        }

        public Builder category(String category) {
            this.currentCategory = category;
            return this;
        }

        public Builder category(String category, String comment) {
            this.currentCategory = category;
            this.config.addCustomCategoryComment(category, comment);
            return this;
        }

        public Builder resetCategory() {
            this.currentCategory = Configuration.CATEGORY_GENERAL;
            return this;
        }

        public Builder loadFile() {
            this.config.load();
            return this;
        }

        public Builder setReloadable() {
            this.reloadable = true;
            return this;
        }

        public Builder addUpdateListener(Consumer<Omniconfig> consumer) {
            this.updateListeners.add(consumer);
            return this;
        }

        public Builder builIncompleteParameters() {
            List<AbstractParameter.Builder<?, ?>> builders = new ArrayList<>();
            builders.addAll(this.incompleteBuilders);

            builders.removeIf(builder -> {
                builder.build();
                return true;
            });

            return this;
        }

        public BooleanParameter.Builder getBoolean(String name, boolean defaultValue) {
            return this.rememberBuilder(BooleanParameter.builder(this, name, defaultValue));
        }

        public IntegerParameter.Builder getInteger(String name, int defaultValue) {
            return this.rememberBuilder(IntegerParameter.builder(this, name, defaultValue));
        }

        public DoubleParameter.Builder getDouble(String name, double defaultValue) {
            return this.rememberBuilder(DoubleParameter.builder(this, name, defaultValue));
        }

        public PerhapsParameter.Builder getPerhaps(String name, Perhaps defaultValue) {
            return this.rememberBuilder(PerhapsParameter.builder(this, name, defaultValue));
        }

        public StringParameter.Builder getString(String name, String defaultValue) {
            return this.rememberBuilder(StringParameter.builder(this, name, defaultValue));
        }

        public StringArrayParameter.Builder getStringArray(String name, String... defaultValue) {
            return this.rememberBuilder(StringArrayParameter.builder(this, name, defaultValue));
        }

        public <T extends Enum<T>> EnumParameter.Builder<T> getEnum(String name, T defaultValue) {
            return this.rememberBuilder(EnumParameter.builder(this, name, defaultValue));
        }

        public Omniconfig build() {
            if (!this.incompleteBuilders.isEmpty()) {
                OmniconfigCore.logger.fatal("Omniconfig builder for file " + this.fileID + " has incomplete parameter builders.");
                OmniconfigCore.logger.fatal("This is an error state. List of incomplete parameter builders goes as following: ");
                for (AbstractParameter.Builder<?, ?> builder : this.incompleteBuilders) {
                    OmniconfigCore.logger.fatal("Class: {}, parameter ID: {}", builder.getClass(), builder.getParameterID());
                }

                throw new RuntimeException("Error when building omniconfig file " + this.fileID + "; incomplete parameter builders remain.");
            }
            return new Omniconfig(this);
        }

        // Internal methods that must not be exposed via API

        private <T extends AbstractParameter.Builder<?, ?>> T rememberBuilder(T builder) {
            this.incompleteBuilders.add(builder);
            return builder;
        }

        public void markBuilderCompleted(AbstractParameter.Builder<?, ?> builder) {
            this.incompleteBuilders.remove(builder);
        }

        public String getPrefix() {
            return this.prefix;
        }

        public boolean isSynchronized() {
            return this.sync;
        }

        public String getCurrentCategory() {
            return this.currentCategory;
        }

        public Configuration getBackingConfig() {
            return this.config;
        }

        public ImmutableMap.Builder<String, AbstractParameter<?>> getPropertyMap() {
            return this.propertyMap;
        }
    }

}
