package io.github.crucible.grimoire.common.core;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.extensibility.IMixinConfig;
import org.spongepowered.asm.mixin.injection.InjectionPoint;

import com.google.gson.annotations.SerializedName;

public class SerializedMixinJson {

    @SerializedName("package")
    private String mixinPackage = null;

    @SerializedName("mixins")
    public List<String> mixinClassesCommon = new ArrayList<>();

    @SerializedName("client")
    public List<String> mixinClassesClient = new ArrayList<>();

    @SerializedName("server")
    public List<String> mixinClassesServer = new ArrayList<>();

    @SerializedName("target")
    private String selector = null;

    @SerializedName("minVersion")
    private String version = "0.7.11";

    @SerializedName("compatibilityLevel")
    private String compatibility = "JAVA_8";

    @SerializedName("required")
    private boolean required = false;

    @SerializedName("priority")
    private int priority = IMixinConfig.DEFAULT_PRIORITY;

    @SerializedName("mixinPriority")
    private int mixinPriority = IMixinConfig.DEFAULT_PRIORITY;

    @SerializedName("setSourceFile")
    private boolean setSourceFile = false;

    @SerializedName("refmap")
    private String refMapperConfig = null;

    @SerializedName("verbose")
    private boolean verboseLogging = false;

    @SerializedName("plugin")
    private String pluginClassName = null;

    @SerializedName("injectors")
    private InjectorOptions injectorOptions = null;

    @SerializedName("overwrites")
    private OverwriteOptions overwriteOptions = null;

    public SerializedMixinJson() {
        // NO-OP
    }

    public void setMixinPackage(String mixinPackage) {
        this.mixinPackage = mixinPackage;
    }

    public void addCommonMixinClasses(String... classes) {
        for (String str : classes) {
            this.mixinClassesCommon.add(str);
        }
    }

    public void addClientMixinClasses(String... classes) {
        for (String str : classes) {
            this.mixinClassesClient.add(str);
        }
    }

    public void addServerMixinClasses(String... classes) {
        for (String str : classes) {
            this.mixinClassesServer.add(str);
        }
    }

    public void setCompatibility(String compatibility) {
        this.compatibility = compatibility;
    }

    public void setMixinPriority(int mixinPriority) {
        this.mixinPriority = mixinPriority;
    }

    public void setPluginClassName(String pluginClassName) {
        this.pluginClassName = pluginClassName;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public void setRefMapperConfig(String refMapperConfig) {
        this.refMapperConfig = refMapperConfig;
    }

    public void setSelector(String selector) {
        this.selector = selector;
    }

    public void setSetSourceFile(boolean setSourceFile) {
        this.setSourceFile = setSourceFile;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setVerboseLogging(boolean verboseLogging) {
        this.verboseLogging = verboseLogging;
    }

    public boolean isValidConfiguration() {
        return this.mixinPackage != null;
    }

    public String getMixinPackage() {
        return this.mixinPackage;
    }

    /**
     * Wrapper for injection options
     */
    static class InjectorOptions {

        @SerializedName("defaultRequire")
        int defaultRequireValue = 0;

        @SerializedName("defaultGroup")
        String defaultGroup = "default";

        @SerializedName("injectionPoints")
        List<String> injectionPoints = null;

        @SerializedName("maxShiftBy")
        int maxShiftBy = InjectionPoint.DEFAULT_ALLOWED_SHIFT_BY;

    }

    /**
     * Wrapper for overwrite options
     */
    static class OverwriteOptions {

        @SerializedName("conformVisibility")
        boolean conformAccessModifiers = false;

        @SerializedName("requireAnnotations")
        boolean requireOverwriteAnnotations = false;

    }

}
