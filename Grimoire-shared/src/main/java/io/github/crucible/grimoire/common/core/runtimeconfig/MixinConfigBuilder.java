package io.github.crucible.grimoire.common.core.runtimeconfig;

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

import io.github.crucible.grimoire.common.api.grimmix.Grimmix;
import io.github.crucible.grimoire.common.api.grimmix.IGrimmix;
import io.github.crucible.grimoire.common.api.mixin.IMixinConfiguration;
import io.github.crucible.grimoire.common.api.mixin.IMixinConfigurationBuilder;
import io.github.crucible.grimoire.common.api.mixin.IMixinConfiguration.ConfigurationType;
import io.github.crucible.grimoire.common.core.GrimmixContainer;
import io.github.crucible.grimoire.common.core.MixinConfiguration;
import io.github.crucible.omniconfig.api.OmniconfigAPI;
import io.github.crucible.omniconfig.core.Omniconfig;

public class MixinConfigBuilder implements IMixinConfigurationBuilder {
    private final SerializedMixinJson json = new SerializedMixinJson();
    private ConfigurationType configType = ConfigurationType.CORE;
    private final String classpath;
    private final IGrimmix owner;

    private List<String> ownerMixinClasses = null;

    public MixinConfigBuilder(IGrimmix owner, String classpath) {
        if (!classpath.endsWith(".json")) {
            classpath = classpath + ".json";
        }

        this.classpath = classpath;
        this.owner = Preconditions.checkNotNull(owner);
    }

    @Override
    public MixinConfigBuilder targetEnvironment(MixinEnvironment.Phase envPhase) {
        this.json.setSelector("@env(" + envPhase.toString() + ")");
        return this;
    }

    @Override
    public MixinConfigBuilder refmap(String refmap) {
        this.json.setRefMapperConfig(refmap);
        return this;
    }

    @Override
    public MixinConfigBuilder required(boolean required) {
        this.json.setRequired(required);
        return this;
    }

    @Override
    public MixinConfigBuilder mixinPackage(String mixinPackage) {
        this.json.setMixinPackage(mixinPackage);
        return this;
    }

    @Override
    public MixinConfigBuilder commonMixins(String... mixinClasses) {
        this.json.addCommonMixinClasses(mixinClasses);
        return this;
    }

    @Override
    public MixinConfigBuilder clientMixins(String... mixinClasses) {
        this.json.addClientMixinClasses(mixinClasses);
        return this;
    }

    @Override
    public MixinConfigBuilder serverMixins(String... mixinClasses) {
        this.json.addServerMixinClasses(mixinClasses);
        return this;
    }

    @Override
    public MixinConfigBuilder priority(int priority) {
        this.json.setPriority(priority);
        return this;
    }

    @Override
    public MixinConfigBuilder mixinPriority(int priority) {
        this.json.setMixinPriority(priority);
        return this;
    }

    @Override
    public MixinConfigBuilder setSourceFile(boolean setOrNot) {
        this.json.setSetSourceFile(setOrNot);
        return this;
    }

    @Override
    public MixinConfigBuilder setConfigurationPlugin(String pluginClass) {
        this.json.setPluginClassName(pluginClass);
        return this;
    }

    @Override
    public MixinConfigBuilder configurationType(ConfigurationType type) {
        this.configType = type;
        return this;
    }

    @Override
    public IMixinConfiguration build() {
        Preconditions.checkArgument(this.json.isValidConfiguration(), "Invalid configuration built. Have you specifed "
                + "all mandatory arguments, like package name and stuff?");

        ConfigBuildingManager.builderRegistry.add(this);
        return new MixinConfiguration(this.owner, this.configType, this.classpath, true);
    }

    // Internal methods that should not be exposed via API

    public File writeAsTempFile() {
        File outFile = null;

        try {
            outFile = File.createTempFile(UUID.randomUUID().toString(), null);

            if (outFile.exists()) {
                outFile.delete();
            }

            this.materializeWildcards();

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

    private void materializeWildcards() {
        this.json.mixinClassesCommon = this.materializeDeclarationList(this.json.mixinClassesCommon);
        this.json.mixinClassesClient = this.materializeDeclarationList(this.json.mixinClassesClient);
        this.json.mixinClassesServer = this.materializeDeclarationList(this.json.mixinClassesServer);
    }

    private List<String> materializeDeclarationList(List<String> list) {
        List<String> materializedList = new ArrayList<>();

        for (String declaration : list) {
            materializedList.addAll(this.matchClassDeclaration(declaration));
        }

        return materializedList;
    }

    private List<String> matchClassDeclaration(String declaration) {
        List<String> matches = new ArrayList<>();

        if (!declaration.contains("*")) {
            matches.add(declaration);
        } else {
            declaration = declaration.replace(".", "\\.").replace("*", ".*");
            for (String cl : this.getOwnerMixinClasses()) {
                if (cl.matches(declaration)) {
                    matches.add(cl);
                }
            }
        }

        return matches;
    }

    private List<String> getOwnerMixinClasses() {
        if (this.ownerMixinClasses == null) {
            this.ownerMixinClasses = this.getOwner().listClassesInPackage(this.json.getMixinPackage());
        }

        return this.ownerMixinClasses;
    }

    public String getClasspath() {
        return this.classpath;
    }

    public GrimmixContainer getOwner() {
        return (GrimmixContainer) this.owner;
    }

}
