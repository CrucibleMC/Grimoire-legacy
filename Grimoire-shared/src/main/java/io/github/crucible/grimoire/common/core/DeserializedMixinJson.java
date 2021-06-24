package io.github.crucible.grimoire.common.core;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import io.github.crucible.grimoire.common.GrimoireCore;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.LoadingStage;
import io.github.crucible.grimoire.common.api.mixin.ConfigurationType;

class DeserializedMixinJson {

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

    @Nullable
    protected static DeserializedMixinJson deserialize(Supplier<InputStream> streamSupplier) {
        DeserializedMixinJson result = null;
        InputStream stream;
        InputStreamReader reader;

        try {
            stream = streamSupplier.get();

            if (stream == null)
                return null;

            Gson gson = new GsonBuilder().create();
            reader = new InputStreamReader(stream, StandardCharsets.UTF_8);

            try {
                DeserializedMixinJson deserialized = gson.fromJson(reader, DeserializedMixinJson.class);
                result = deserialized;
            } catch (Exception ex) {
                // Likely we got invalid json, just proceed with out lifes
            }

            reader.close();
            stream.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return result;
    }
}
