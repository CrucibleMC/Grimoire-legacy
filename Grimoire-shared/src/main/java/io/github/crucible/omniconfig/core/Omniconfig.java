package io.github.crucible.omniconfig.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import io.github.crucible.omniconfig.OmniconfigCore;
import io.github.crucible.omniconfig.api.builders.IOmniconfigBuilder;
import io.github.crucible.omniconfig.api.core.IOmniconfig;
import io.github.crucible.omniconfig.api.core.SidedConfigType;
import io.github.crucible.omniconfig.api.core.VersioningPolicy;
import io.github.crucible.omniconfig.api.lib.Perhaps;
import io.github.crucible.omniconfig.api.lib.Version;
import io.github.crucible.omniconfig.api.properties.IAbstractProperty;
import io.github.crucible.omniconfig.backing.Configuration;
import io.github.crucible.omniconfig.core.properties.AbstractParameter;
import io.github.crucible.omniconfig.core.properties.BooleanParameter;
import io.github.crucible.omniconfig.core.properties.DoubleParameter;
import io.github.crucible.omniconfig.core.properties.EnumParameter;
import io.github.crucible.omniconfig.core.properties.FloatParameter;
import io.github.crucible.omniconfig.core.properties.IntegerParameter;
import io.github.crucible.omniconfig.core.properties.PerhapsParameter;
import io.github.crucible.omniconfig.core.properties.StringArrayParameter;
import io.github.crucible.omniconfig.core.properties.StringParameter;

public class Omniconfig implements IOmniconfig {
    protected final Configuration config;
    protected final String fileID;
    protected final ImmutableMap<String, IAbstractProperty> propertyMap;
    protected final ImmutableList<Consumer<IOmniconfig>> updateListeners;
    protected final boolean reloadable;

    protected Omniconfig(Builder builder) {
        this.config = builder.config;
        this.reloadable = builder.reloadable;
        this.fileID = builder.fileID;
        this.propertyMap = builder.propertyMap.build();
        this.updateListeners = builder.updateListeners.build();

        this.config.save();

        OmniconfigCore.INSTANCE.backUpDefaultCopy(this);

        if (this.reloadable || this.updateListeners.size() > 0) {
            this.config.attachBeholder();
            this.config.attachReloadingAction(this::onConfigReload);
        }

        OmniconfigRegistry.INSTANCE.registerConfig(this);

        OmniconfigCore.logger.info("Sucessfully build Omniconfig file {} with total of {} properties.",
                this.fileID, this.propertyMap.size());
    }

    @Override
    public Collection<IAbstractProperty> getLoadedParameters() {
        return this.propertyMap.values();
    }

    @Override
    public Optional<IAbstractProperty> getParameter(String parameterID) {
        return Optional.ofNullable(this.propertyMap.get(parameterID));
    }

    @Override
    public void forceReload() {
        this.config.load();
        this.updateListeners.forEach(listener -> listener.accept(this));
    }

    @Override
    public boolean isReloadable() {
        return this.reloadable;
    }

    @Override
    public File getFile() {
        return this.config.getConfigFile();
    }

    @Override
    public String getFileName() {
        return this.getFile().getName();
    }

    @Override
    public String getFileID() {
        return this.fileID;
    }

    @Override
    public Version getVersion() {
        return this.config.getLoadedConfigVersion();
    }

    @Override
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
                AbstractParameter<?> param = (AbstractParameter<?>) entry.getValue();

                if (!OmniconfigCore.onRemoteServer || !param.isSynchronized()) {
                    param.reloadFrom(this);
                }
            });
        }

        this.updateListeners.forEach(listener -> listener.accept(this));
    }

    // Builder starting methods

    public static Builder builder(String fileName) {
        return builder(fileName, new Version("1.0.0"));
    }

    public static Builder builder(String fileName, Version version) {
        return builder(fileName, version, false);
    }

    public static Builder builder(String fileName, Version version, boolean caseSensitive) {
        return builder(fileName, version, caseSensitive, SidedConfigType.COMMON);
    }

    public static Builder builder(String fileName, Version version, boolean caseSensitive, SidedConfigType sidedType) {
        try {
            File file = new File(OmniconfigCore.CONFIG_DIR, fileName+".cfg");
            String filePath = file.getCanonicalPath();
            String configDirPath = OmniconfigCore.CONFIG_DIR.getCanonicalPath();

            if (!filePath.startsWith(configDirPath))
                throw new IOException("Requested config file [" + filePath + "] resides outside of default configuration directory ["
                        + configDirPath + "]. This is strictly forbidden.");

            String fileID = filePath.replace(configDirPath + OmniconfigCore.FILE_SEPARATOR, "");

            return new Builder(fileID, new Configuration(file, version, caseSensitive), sidedType);
        } catch (Exception ex) {
            throw new RuntimeException("Something screwed up when loading config!", ex);
        }
    }

    // Builder class

    public static class Builder implements IOmniconfigBuilder {
        protected final Configuration config;
        protected final String fileID;
        protected final ImmutableMap.Builder<String, IAbstractProperty> propertyMap = ImmutableMap.builder();
        protected final ImmutableList.Builder<Consumer<IOmniconfig>> updateListeners = ImmutableList.builder();
        protected final List<AbstractParameter.Builder<?, ?>> incompleteBuilders = new ArrayList<>();

        protected String currentCategory = "";
        protected String prefix = "";
        protected boolean reloadable = false;
        protected boolean sync = false;

        protected Function<Version, VersioningPolicy> versioningPolicyBackflips = null;
        protected Configuration oldDefaultCopy = null;

        protected Builder(String fileID, Configuration config, SidedConfigType sidedType) {
            this.config = config;
            this.fileID = fileID;

            this.config.setSidedType(sidedType);

            OmniconfigCore.logger.info("Started Omniconfig builder for file: " + fileID);
        }

        @Override
        public Builder versioningPolicy(VersioningPolicy policy) {
            this.config.setVersioningPolicy(policy);
            return this;
        }

        @Override
        public Builder terminateNonInvokedKeys(boolean terminate) {
            this.config.setTerminateNonInvokedKeys(terminate);
            return this;
        }

        @Override
        public Builder versioningPolicyBackflips(Function<Version, VersioningPolicy> determinator) {
            this.versioningPolicyBackflips = determinator;
            return this;
        }

        @Override
        public Builder loadFile() {
            this.config.load();

            if (this.versioningPolicyBackflips != null) {
                this.config.setVersioningPolicy(this.versioningPolicyBackflips.apply(this.config.getLoadedConfigVersion()));
            }

            if (this.config.loadingOutdatedFile()) {
                VersioningPolicy policy = this.config.getVersioningPolicy();
                if (policy == VersioningPolicy.RESPECTFUL || policy == VersioningPolicy.NOBLE) {
                    try {
                        File defaultCopy = OmniconfigCore.INSTANCE.extractDefaultCopy(this.fileID);

                        if (defaultCopy != null && defaultCopy.exists() && defaultCopy.isFile()) {
                            this.oldDefaultCopy = new Configuration(defaultCopy, this.config.getDefinedConfigVersion(), this.config.сaseSensitiveCustomCategories());
                            this.oldDefaultCopy.setVersioningPolicy(VersioningPolicy.DISMISSIVE);
                            this.oldDefaultCopy.markTemporary();
                            this.oldDefaultCopy.load();
                            this.oldDefaultCopy.resetFileVersion();

                            OmniconfigCore.logger.info("Sucessfully loaded default backup file for omniconfig {}, file path: {}", this.fileID, defaultCopy.getCanonicalPath());
                        } else {
                            OmniconfigCore.logger.info("Could not extract default copy of config file {}", this.fileID);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }

            this.config.resetFileVersion();
            return this;
        }

        @Override
        public Builder prefix(String prefix) {
            this.prefix = prefix;
            return this;
        }

        @Override
        public Builder resetPrefix() {
            this.prefix = "";
            return this;
        }

        @Override
        public Builder pushCategory(String category) {
            return this.pushCategory(category, null);
        }

        @Override
        public Builder pushCategory(String category, String comment) {
            category = this.clearCategorySplitters(category);

            if (!this.currentCategory.isEmpty()) {
                this.currentCategory += Configuration.CATEGORY_SPLITTER + category;
            } else {
                this.currentCategory = category;
            }

            if (comment != null) {
                this.config.addCustomCategoryComment(this.currentCategory, comment);
            }

            return this;
        }

        @Override
        public Builder popCategory() {
            if (this.currentCategory.contains(Configuration.CATEGORY_SPLITTER)) {
                this.currentCategory = this.currentCategory.substring(0, this.currentCategory.lastIndexOf(Configuration.CATEGORY_SPLITTER));
            } else {
                this.currentCategory = "";
            }
            return this;
        }

        @Override
        public Builder resetCategory() {
            this.currentCategory = "";
            return this;
        }

        @Override
        public Builder synchronize(boolean sync) {
            this.sync = sync;
            return this;
        }

        @Override
        public BooleanParameter.Builder getBoolean(String name, boolean defaultValue) {
            this.assertPushedCategory();
            return this.rememberBuilder(BooleanParameter.builder(this, name, defaultValue));
        }

        @Override
        public IntegerParameter.Builder getInteger(String name, int defaultValue) {
            this.assertPushedCategory();
            return this.rememberBuilder(IntegerParameter.builder(this, name, defaultValue));
        }

        @Override
        public DoubleParameter.Builder getDouble(String name, double defaultValue) {
            this.assertPushedCategory();
            return this.rememberBuilder(DoubleParameter.builder(this, name, defaultValue));
        }

        @Override
        public FloatParameter.Builder getFloat(String name, float defaultValue) {
            this.assertPushedCategory();
            return this.rememberBuilder(FloatParameter.builder(this, name, defaultValue));
        }

        @Override
        public PerhapsParameter.Builder getPerhaps(String name, Perhaps defaultValue) {
            this.assertPushedCategory();
            return this.rememberBuilder(PerhapsParameter.builder(this, name, defaultValue));
        }

        @Override
        public StringParameter.Builder getString(String name, String defaultValue) {
            this.assertPushedCategory();
            return this.rememberBuilder(StringParameter.builder(this, name, defaultValue));
        }

        @Override
        public StringArrayParameter.Builder getStringList(String name, String... defaultValue) {
            this.assertPushedCategory();
            return this.rememberBuilder(StringArrayParameter.builder(this, name, defaultValue));
        }

        @Override
        public <T extends Enum<T>> EnumParameter.Builder<T> getEnum(String name, T defaultValue) {
            this.assertPushedCategory();
            return this.rememberBuilder(EnumParameter.builder(this, name, defaultValue));
        }

        @Override
        public Builder setReloadable() {
            this.reloadable = true;
            return this;
        }

        @Override
        public Builder addUpdateListener(Consumer<IOmniconfig> consumer) {
            this.updateListeners.add(consumer);
            return this;
        }

        @Override
        public Builder buildIncompleteParameters() {
            List<AbstractParameter.Builder<?, ?>> builders = new ArrayList<>();
            builders.addAll(this.incompleteBuilders);

            builders.removeIf(builder -> {
                builder.build();
                return true;
            });

            return this;
        }

        @Override
        public Omniconfig build() {
            if (!this.incompleteBuilders.isEmpty()) {
                OmniconfigCore.logger.fatal("Omniconfig builder for file " + this.fileID + " has incomplete parameter builders.");
                OmniconfigCore.logger.fatal("This is an error state. List of incomplete parameter builders goes as following: ");
                for (AbstractParameter.Builder<?, ?> builder : this.incompleteBuilders) {
                    OmniconfigCore.logger.fatal("Class: {}, parameter ID: {}", builder.getClass(), builder.getParameterID());
                }

                throw new RuntimeException("Error when building omniconfig file " + this.fileID + "; incomplete parameter builders remain.");
            }

            if (this.oldDefaultCopy != null) {
                this.oldDefaultCopy.getConfigFile().delete();
                this.oldDefaultCopy = null;
                OmniconfigCore.logger.info("Finished updating default values for config {}, deleted temporary default copy.", this.fileID);
            }

            return new Omniconfig(this);
        }

        // Internal methods that must not be exposed via API

        private void assertPushedCategory() {
            if (this.currentCategory.isEmpty())
                throw new IllegalArgumentException("Cannot create config property without any category specified.");
        }

        private String clearCategorySplitters(String str) {
            return str.replace(Configuration.CATEGORY_SPLITTER, "");
        }

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

        public Configuration getDefaultConfigCopy() {
            return this.oldDefaultCopy;
        }

        public boolean updatingOldConfig() {
            return this.getDefaultConfigCopy() != null && this.config.loadingOutdatedFile();
        }

        public ImmutableMap.Builder<String, IAbstractProperty> getPropertyMap() {
            return this.propertyMap;
        }

        public boolean isReloadable() {
            return this.reloadable;
        }

        // TODO Wiki page explaining why both Omniconfig.Builder and @AnnotationConfig are useful

    }

}
