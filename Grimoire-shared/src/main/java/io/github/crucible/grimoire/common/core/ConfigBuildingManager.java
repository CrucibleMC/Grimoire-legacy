package io.github.crucible.grimoire.common.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;

import cpw.mods.fml.common.FMLCommonHandler;
import io.github.crucible.grimoire.common.GrimoireInternals;
import io.github.crucible.omniconfig.OmniconfigCore;
import io.github.crucible.omniconfig.backing.Configuration;
import io.github.crucible.omniconfig.lib.FileWatcher;

public class ConfigBuildingManager {
    protected static final List<MixinConfigBuilder> builderRegistry = new ArrayList<>();
    private static final File mixinConfigsArchive = new File(GrimoireCore.INSTANCE.getDataFolder(), "RuntimeMixinConfigs.jar");

    private ConfigBuildingManager() {
        // NO-OP
    }

    public static void generateRuntimeConfigurations() {
        try {
            if (mixinConfigsArchive.exists()) {
                if (!mixinConfigsArchive.delete())
                    throw new IOException("Could not delete outdated runtime mixin configs archive: " + mixinConfigsArchive.getCanonicalPath());
            }

            byte[] buf = new byte[1024];
            JarOutputStream jarOutput = new JarOutputStream(new FileOutputStream(mixinConfigsArchive));

            for (MixinConfigBuilder builder : builderRegistry) {
                File config = builder.writeAsTempFile();
                InputStream updateInput = new FileInputStream(config);

                jarOutput.putNextEntry(new JarEntry(builder.getClasspath()));
                int len;
                while ((len = updateInput.read(buf)) > 0) {
                    jarOutput.write(buf, 0, len);
                }

                updateInput.close();
                config.delete();
            }

            jarOutput.close();

            RuntimeMixinsProtector.create(getArchiveMD5Digest()).attach();

            GrimoireCore.INSTANCE.getClassLoader().addURL(mixinConfigsArchive.toURI().toURL());
        } catch (Exception ex) {
            Throwables.propagate(ex);
        }
    }

    private static String getArchiveMD5Digest() {
        return GrimoireInternals.getMD5Digest(mixinConfigsArchive);
    }

    private static class RuntimeMixinsProtector implements Runnable {
        private static RuntimeMixinsProtector INSTANCE = null;

        private final String archiveMD5;
        private final ClassLoader realClassLoader;

        private RuntimeMixinsProtector(String archiveMD5) {
            Preconditions.checkArgument(INSTANCE == null, "Beholder already instantiated!");

            this.realClassLoader = Thread.currentThread().getContextClassLoader();
            this.archiveMD5 = archiveMD5;

            INSTANCE = this;
        }

        @Override
        public void run() {
            Thread.currentThread().setContextClassLoader(this.realClassLoader);

            String newDigest = getArchiveMD5Digest();

            if (!Objects.equal(this.archiveMD5, newDigest)) {
                GrimoireCore.logger.fatal("Runtime mixin configs archive was illegally modified! Memorized MD5: {}, modified MD5: {}",
                        this.archiveMD5, newDigest);
                GrimoireCore.logger.fatal("Forcing JVM termination...");

                FMLCommonHandler.instance().exitJava(-1711635183, false);
            }
        }

        public void attach() {
            try {
                FileWatcher.defaultInstance().addWatch(mixinConfigsArchive, this);
            } catch (Exception ex) {
                Throwables.propagate(ex);
            }
        }

        public void detach() {
            try {
                FileWatcher.defaultInstance().removeWatch(mixinConfigsArchive);
            } catch (Exception ex) {
                Throwables.propagate(ex);
            }
        }

        public static RuntimeMixinsProtector create(String archiveMD5) {
            if (INSTANCE == null) {
                new RuntimeMixinsProtector(archiveMD5);
            }

            return INSTANCE;
        }
    }
}