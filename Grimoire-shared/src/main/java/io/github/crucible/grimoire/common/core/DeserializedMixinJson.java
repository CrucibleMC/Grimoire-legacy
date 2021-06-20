package io.github.crucible.grimoire.common.core;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class DeserializedMixinJson {

    // TODO forceLoadAtStage thing

    @SerializedName("package")
    private String mixinPackage;

    @SerializedName("mixins")
    private List<String> mixinClasses;

    @SerializedName("client")
    private List<String> mixinClassesClient;

    @SerializedName("server")
    private List<String> mixinClassesServer;

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
}
