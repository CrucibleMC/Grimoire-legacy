package io.github.crucible.grimoire.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import io.github.crucible.grimoire.common.api.lib.Side;
import io.github.crucible.grimoire.common.core.GrimoireCore;
import io.github.crucible.grimoire.common.core.MixinJson;

public class GrimoireInternals {

    public static void executeInEnvironment(Side side, Supplier<Runnable> supplier) {
        if (side == getEnvironment()) {
            supplier.get().run();
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> void ifInstance(Object obj, Class<T> ofClass, Consumer<T> consumer) {
        if (ofClass.isAssignableFrom(obj.getClass())) {
            consumer.accept((T) obj);
        }
    }

    public static Side getEnvironment() {
        return GrimoireCore.INSTANCE.getEnvironment();
    }

    public static boolean isMixinConfiguration(Supplier<InputStream> streamSupplier) {
        MixinJson result = null;

        try {
            InputStream stream = streamSupplier.get();

            if (stream == null)
                return false;

            Gson gson = new GsonBuilder().create();
            InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
            result = gson.fromJson(reader, MixinJson.class);
            reader.close();
            stream.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return result != null && result.isValidConfiguration();
    }

}