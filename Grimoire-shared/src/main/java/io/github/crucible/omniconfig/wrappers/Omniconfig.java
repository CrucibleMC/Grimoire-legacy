package io.github.crucible.omniconfig.wrappers;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import io.github.crucible.omniconfig.OmniconfigCore;
import io.github.crucible.omniconfig.core.Configuration;
import io.github.crucible.omniconfig.core.Configuration.SidedConfigType;
import io.github.crucible.omniconfig.core.Configuration.VersioningPolicy;
import io.github.crucible.omniconfig.wrappers.values.AbstractParameter;

public class Omniconfig {
    protected static final Map<String, Omniconfig> configRegistry = new HashMap<>();

    protected final Configuration config;
    protected final Map<String, AbstractParameter<?>> propertyMap;
    protected final boolean reloadable;

    protected Omniconfig(Configuration config, Map<String, AbstractParameter<?>> propertyMap, boolean reloadable) {
        this.config = config;
        this.propertyMap = propertyMap;
        this.reloadable = reloadable;

        config.save();
        configRegistry.put(config.getConfigFile().getName(), this);
    }

    // Builder starting methods

    public static Builder builder(String fileName) {
        return builder(fileName, null);
    }

    public static Builder builder(String fileName, String version) {
        return builder(fileName, false, version);
    }

    public static Builder builder(String fileName, boolean caseSensitive, String version) {
        try {
            return builder(new File(OmniconfigCore.CONFIG_DIR, fileName+".omniconf"), caseSensitive, version);
        } catch (Exception ex) {
            new RuntimeException("Something screwed up when loading config.", ex).printStackTrace();
            return null;
        }
    }

    public static Builder builder(File file) {
        return builder(file, null);
    }

    public static Builder builder(File file, String version) {
        return builder(file, false, version);
    }

    public static Builder builder(File file, boolean caseSensitive, String version) {
        return new Builder(new Configuration(file, version, caseSensitive));
    }

    public static Builder builder(Configuration config) {
        return new Builder(config);
    }

    // Builder class

    public static class Builder {
        protected final Configuration config;
        protected String currentCategory = Configuration.CATEGORY_GENERAL;
        protected String prefix = "";
        protected boolean sync = false;
        protected boolean reloadable = false;
        protected ImmutableMap.Builder<String, AbstractParameter<?>> propertyMap = ImmutableMap.builder();

        protected Builder(Configuration config) {
            this.config = config;
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

        public Builder sided(SidedConfigType type) {
            this.config.setSidedType(type);
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

        public Omniconfig build() {
            return new Omniconfig(this.config, this.propertyMap.build(), this.reloadable);
        }
    }

}
