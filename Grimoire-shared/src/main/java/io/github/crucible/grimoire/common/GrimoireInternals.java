package io.github.crucible.grimoire.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.Nullable;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import io.github.crucible.grimoire.common.api.lib.Environment;
import io.github.crucible.grimoire.common.core.DeserializedMixinJson;

public class GrimoireInternals {

    public static void executeInEnvironment(Environment side, Supplier<Runnable> supplier) {
        side.execute(supplier);
    }

    @SuppressWarnings("unchecked")
    public static <T> void ifInstance(Object obj, Class<T> ofClass, Consumer<T> consumer) {
        if (ofClass.isAssignableFrom(obj.getClass())) {
            consumer.accept((T) obj);
        }
    }

    public static Environment getEnvironment() {
        return GrimoireCore.INSTANCE.getEnvironment();
    }

    @Nullable
    public static DeserializedMixinJson deserializeMixinConfiguration(Supplier<InputStream> streamSupplier) {
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
                result = gson.fromJson(reader, DeserializedMixinJson.class);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            reader.close();
            stream.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return result;
    }

    public static String getMD5Digest(File file) {
        if (file.exists() && file.isFile()) {
            try {
                InputStream stream = new FileInputStream(file);
                String md5 = DigestUtils.md5Hex(stream);
                stream.close();

                return md5;
            } catch (Exception ex) {
                Throwables.propagate(ex);
            }
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> void cast(Object object, Class<T> castClass, Consumer<T> onCast) {
        onCast.accept((T)object);
    }

}