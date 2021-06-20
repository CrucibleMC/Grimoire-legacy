package io.github.crucible.grimoire.common.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.spongepowered.asm.mixin.MixinEnvironment;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.github.crucible.grimoire.common.api.configurations.IMixinConfiguration;
import io.github.crucible.grimoire.common.api.configurations.IMixinConfiguration.ConfigurationType;
import io.github.crucible.grimoire.common.api.grimmix.Grimmix;
import io.github.crucible.grimoire.common.api.grimmix.IGrimmix;
import io.github.crucible.omniconfig.api.OmniconfigAPI;
import io.github.crucible.omniconfig.core.Omniconfig;

public class MixinConfigBuilder {
    private final SerializedMixinJson json = new SerializedMixinJson();
    private ConfigurationType configType = ConfigurationType.CORE;
    private final String classpath;
    private final IGrimmix owner;

    public MixinConfigBuilder(IGrimmix owner, String classpath) {
        if (!classpath.endsWith(".json")) {
            classpath = classpath + ".json";
        }
        // fileName = fileName.replace("/", OmniconfigAPI.getFileSeparator());

        this.classpath = classpath;
        this.owner = owner;
    }

    public MixinConfigBuilder targetEnvironment(MixinEnvironment.Phase envPhase) {
        this.json.setSelector("@env(" + envPhase.toString() + ")");
        return this;
    }

    public MixinConfigBuilder refmap(String refmap) {
        this.json.setRefMapperConfig(refmap);
        return this;
    }

    public MixinConfigBuilder required(boolean required) {
        this.json.setRequired(required);
        return this;
    }

    public MixinConfigBuilder mixinPackage(String mixinPackage) {
        this.json.setMixinPackage(mixinPackage);
        return this;
    }

    public MixinConfigBuilder commonMixins(String... mixinClasses) {
        this.json.addCommonMixinClasses(mixinClasses);
        return this;
    }

    public MixinConfigBuilder clientMixins(String... mixinClasses) {
        this.json.addClientMixinClasses(mixinClasses);
        return this;
    }

    public MixinConfigBuilder serverMixins(String... mixinClasses) {
        this.json.addServerMixinClasses(mixinClasses);
        return this;
    }

    public MixinConfigBuilder priority(int priority) {
        this.json.setPriority(priority);
        return this;
    }

    public MixinConfigBuilder mixinPriority(int priority) {
        this.json.setMixinPriority(priority);
        return this;
    }

    public MixinConfigBuilder setSourceFile(boolean setOrNot) {
        this.json.setSetSourceFile(setOrNot);
        return this;
    }

    public MixinConfigBuilder setConfigurationPlugin(String pluginClass) {
        this.json.setPluginClassName(pluginClass);
        return this;
    }

    public void configurationType(ConfigurationType type) {
        this.configType = type;
    }

    public IMixinConfiguration build() {
        Preconditions.checkArgument(this.json.isValidConfiguration(), "Invalid configuration built. Have you specifed "
                + "all mandatory arguments, like package name and stuff?");

        ConfigBuildingManager.builderRegistry.add(this);

        //MixinConfiguration config = new MixinConfiguration(this.owner, this.configType, this.classpath);
        //return config.isDuplicate() ? config.getDuplicateOf() : config;
        return null;
    }

    // Internal methods that should not be exposed via API

    public File writeAsTempFile() {
        File outFile = null;

        try {
            outFile = File.createTempFile(UUID.randomUUID().toString(), null);

            if (outFile.exists()) {
                outFile.delete();
            }

            OutputStream outStream = new FileOutputStream(outFile);
            OutputStreamWriter outWriter = new OutputStreamWriter(outStream);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(this.json, outWriter);

            outWriter.close();
            outStream.close();
        } catch (Exception ex) {
            Throwables.propagate(ex);
        }

        return outFile;
    }

    public String getClasspath() {
        return this.classpath;
    }

}
