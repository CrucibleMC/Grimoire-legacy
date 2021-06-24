package io.github.crucible.grimoire.common.core;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.google.gson.annotations.SerializedName;

import io.github.crucible.grimoire.common.GrimoireCore;
import io.github.crucible.grimoire.common.api.configurations.IMixinConfiguration.ConfigurationType;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.LoadingStage;

public class DeserializedMixinJson {

    @SerializedName("package")
    private String mixinPackage;

    @SerializedName("mixins")
    private List<String> mixinClasses;

    @SerializedName("client")
    private List<String> mixinClassesClient;

    @SerializedName("server")
    private List<String> mixinClassesServer;

    @SerializedName("forceLoadAtStage")
    private String forceLoadAtStage;

    private DeserializedMixinJson() {
        // NO-OP
    }

    public boolean isValidConfiguration() {
        boolean valid = this.mixinPackage != null && this.mixinClasses != null && this.mixinClassesClient != null && this.mixinClassesServer != null;

        if (valid) {
            GrimoireCore.logger.info("Deserialized MixinJson. Package: {}, mixins: {}, client: {}, server: {}",
                    this.mixinPackage, this.mixinClasses, this.mixinClassesClient, this.mixinClassesServer);
        }

        return valid;
    }

    @Nullable
    public ConfigurationType getForceLoadType() {
        if (this.forceLoadAtStage != null) {
            try {
                LoadingStage forcedStage = LoadingStage.valueOf(this.forceLoadAtStage);
                return forcedStage.getAssociatedConfigurationType();
            } catch (Exception ex) {
                return null;
            }
        } else
            return null;
    }
}
