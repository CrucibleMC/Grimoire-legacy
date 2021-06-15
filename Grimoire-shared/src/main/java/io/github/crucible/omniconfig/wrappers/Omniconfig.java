package io.github.crucible.omniconfig.wrappers;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import io.github.crucible.omniconfig.OmniconfigCore;
import io.github.crucible.omniconfig.core.Configuration;
import io.github.crucible.omniconfig.core.SynchronizationManager;
import io.github.crucible.omniconfig.core.Configuration.SidedConfigType;
import io.github.crucible.omniconfig.core.Configuration.VersioningPolicy;
import io.github.crucible.omniconfig.lib.Perhaps;
import io.github.crucible.omniconfig.lib.Version;
import io.github.crucible.omniconfig.wrappers.values.AbstractParameter;
import io.github.crucible.omniconfig.wrappers.values.BooleanParameter;
import io.github.crucible.omniconfig.wrappers.values.DoubleParameter;
import io.github.crucible.omniconfig.wrappers.values.EnumParameter;
import io.github.crucible.omniconfig.wrappers.values.FloatParameter;
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

        OmniconfigCore.INSTANCE.backUpDefaultCopy(this);

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

    public Version getVersion() {
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
        protected final String fileID;
        protected final ImmutableMap.Builder<String, AbstractParameter<?>> propertyMap = ImmutableMap.builder();
        protected final ImmutableList.Builder<Consumer<Omniconfig>> updateListeners = ImmutableList.builder();
        protected final List<AbstractParameter.Builder<?, ?>> incompleteBuilders = new ArrayList<>();

        protected String currentCategory = Configuration.CATEGORY_GENERAL;
        protected String prefix = "";
        protected boolean reloadable = false;
        protected boolean sync = false;

        protected Function<Version, VersioningPolicy> versioningPolicyBackflips = null;
        protected Configuration oldDefaultCopy = null;

        protected Builder(String fileID, Configuration config, SidedConfigType sidedType) {
            this.config = config;
            this.fileID = fileID;

            this.config.setSidedType(sidedType);
        }

        @PhaseOnly(BuildingPhase.INITIALIZATION)
        public Builder versioningPolicy(VersioningPolicy policy) {
            this.config.setVersioningPolicy(policy);
            return this;
        }

        @PhaseOnly(BuildingPhase.INITIALIZATION)
        public Builder terminateNonInvokedKeys(boolean terminate) {
            this.config.setTerminateNonInvokedKeys(terminate);
            return this;
        }

        @PhaseOnly(BuildingPhase.INITIALIZATION)
        public Builder versioningPolicyBackflips(Function<Version, VersioningPolicy> determinator) {
            this.versioningPolicyBackflips = determinator;
            return this;
        }

        @PhaseOnly(BuildingPhase.INITIALIZATION)
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

        @PhaseOnly(BuildingPhase.PARAMETER_LOADING)
        public Builder prefix(String prefix) {
            this.prefix = prefix;
            return this;
        }

        @PhaseOnly(BuildingPhase.PARAMETER_LOADING)
        public Builder resetPrefix() {
            this.prefix = "";
            return this;
        }

        @PhaseOnly(BuildingPhase.PARAMETER_LOADING)
        public Builder category(String category) {
            this.currentCategory = category;
            return this;
        }

        @PhaseOnly(BuildingPhase.PARAMETER_LOADING)
        public Builder category(String category, String comment) {
            this.currentCategory = category;
            this.config.addCustomCategoryComment(category, comment);
            return this;
        }

        @PhaseOnly(BuildingPhase.PARAMETER_LOADING)
        public Builder resetCategory() {
            this.currentCategory = Configuration.CATEGORY_GENERAL;
            return this;
        }

        @PhaseOnly(BuildingPhase.PARAMETER_LOADING)
        public Builder synchronize(boolean sync) {
            this.sync = sync;
            return this;
        }

        @PhaseOnly(BuildingPhase.PARAMETER_LOADING)
        public BooleanParameter.Builder getBoolean(String name, boolean defaultValue) {
            return this.rememberBuilder(BooleanParameter.builder(this, name, defaultValue));
        }

        @PhaseOnly(BuildingPhase.PARAMETER_LOADING)
        public IntegerParameter.Builder getInteger(String name, int defaultValue) {
            return this.rememberBuilder(IntegerParameter.builder(this, name, defaultValue));
        }

        @PhaseOnly(BuildingPhase.PARAMETER_LOADING)
        public DoubleParameter.Builder getDouble(String name, double defaultValue) {
            return this.rememberBuilder(DoubleParameter.builder(this, name, defaultValue));
        }

        @PhaseOnly(BuildingPhase.PARAMETER_LOADING)
        public FloatParameter.Builder getFloat(String name, float defaultValue) {
            return this.rememberBuilder(FloatParameter.builder(this, name, defaultValue));
        }

        @PhaseOnly(BuildingPhase.PARAMETER_LOADING)
        public PerhapsParameter.Builder getPerhaps(String name, Perhaps defaultValue) {
            return this.rememberBuilder(PerhapsParameter.builder(this, name, defaultValue));
        }

        @PhaseOnly(BuildingPhase.PARAMETER_LOADING)
        public StringParameter.Builder getString(String name, String defaultValue) {
            return this.rememberBuilder(StringParameter.builder(this, name, defaultValue));
        }

        @PhaseOnly(BuildingPhase.PARAMETER_LOADING)
        public StringArrayParameter.Builder getStringArray(String name, String... defaultValue) {
            return this.rememberBuilder(StringArrayParameter.builder(this, name, defaultValue));
        }

        @PhaseOnly(BuildingPhase.PARAMETER_LOADING)
        public <T extends Enum<T>> EnumParameter.Builder<T> getEnum(String name, T defaultValue) {
            return this.rememberBuilder(EnumParameter.builder(this, name, defaultValue));
        }

        @PhaseOnly(BuildingPhase.FINALIZATION)
        public Builder setReloadable() {
            this.reloadable = true;
            return this;
        }

        @PhaseOnly(BuildingPhase.FINALIZATION)
        public Builder addUpdateListener(Consumer<Omniconfig> consumer) {
            this.updateListeners.add(consumer);
            return this;
        }

        @PhaseOnly(BuildingPhase.FINALIZATION)
        public Builder buildIncompleteParameters() {
            List<AbstractParameter.Builder<?, ?>> builders = new ArrayList<>();
            builders.addAll(this.incompleteBuilders);

            builders.removeIf(builder -> {
                builder.build();
                return true;
            });

            return this;
        }

        @PhaseOnly(BuildingPhase.FINALIZATION)
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

        public ImmutableMap.Builder<String, AbstractParameter<?>> getPropertyMap() {
            return this.propertyMap;
        }

        // TODO Move this to API when we make one and document
        // TODO Wiki page explaining why both Omniconfig.Builder and @AnnotationConfig are useful
        protected static enum BuildingPhase {
            INITIALIZATION,
            PARAMETER_LOADING,
            FINALIZATION;
        }

        @Target(ElementType.METHOD)
        @Retention(RetentionPolicy.CLASS)
        protected static @interface PhaseOnly {
            BuildingPhase[] value();
        }
    }

}
