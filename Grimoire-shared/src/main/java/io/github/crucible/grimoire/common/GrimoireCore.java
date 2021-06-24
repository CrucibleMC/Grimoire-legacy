package io.github.crucible.grimoire.common;

import io.github.crucible.grimoire.common.api.grimmix.IGrimmix;
import io.github.crucible.grimoire.common.api.lib.Environment;
import io.github.crucible.grimoire.common.core.GrimmixLoader;
import io.github.crucible.grimoire.common.test.AnnotationConfigTest;
import io.github.crucible.grimoire.common.test.OmniconfigTest;
import io.github.crucible.omniconfig.OmniconfigCore;
import io.github.crucible.omniconfig.gconfig.AnnotationConfigCore;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Objects;
import com.google.common.base.Throwables;

import java.io.File;
import java.lang.reflect.Field;

public class GrimoireCore {
    public static final GrimoireCore INSTANCE = new GrimoireCore();
    public static final Logger logger = LogManager.getLogger("Grimoire");
    private static final LaunchClassLoader classLoader = (LaunchClassLoader) GrimoireCore.class.getClassLoader();

    private final GrimmixLoader grimmixLoader;
    private File mcLocation;
    private File mcModFolder;
    private File configFolder;
    private File dataFolder;
    private String version;
    private Environment side;
    private Boolean obfuscated = null;
    private Class<?> coremodManager = null;

    public GrimoireCore() {
        if (this.isDevEnvironment()) {
            logger.info("Loading within deobfuscated environment...");

            System.setProperty("mixin.debug", "true");
            System.setProperty("mixin.hotSwap", "true");
            System.setProperty("mixin.env.disableRefMap", "true");
            System.setProperty("mixin.debug.export", "true");
            System.setProperty("mixin.checks", "true");
            System.setProperty("mixin.checks.interfaces", "true");
            System.setProperty("mixin.env", "true");
        }

        this.grimmixLoader = GrimmixLoader.INSTANCE;
    }

    public void setCoremodManager(Class<?> coreModManager) {
        this.coremodManager = coreModManager;
    }

    public void configure(File mcLocation, String mcModFolder, String version, Environment onSide) {
        this.side = onSide;
        this.version = version;
        this.mcLocation = mcLocation;
        this.mcModFolder = new File(this.mcLocation, mcModFolder);
        this.configFolder = new File(this.mcLocation, "config");
        this.dataFolder = new File(this.mcLocation, "mcdata");

        this.configFolder.mkdirs();
        this.dataFolder.mkdirs();

        OmniconfigCore.logger.info("Initializing omniconfig core...");
    }

    public void init() {
        OmniconfigTest.INSTANCE.getClass(); // make it construct
        AnnotationConfigCore.INSTANCE.addAnnotationConfig(AnnotationConfigTest.class);

        this.grimmixLoader.scanForGrimmixes(classLoader, this.mcModFolder, new File(this.mcModFolder, this.version));

        this.grimmixLoader.construct();
        this.grimmixLoader.validate();
        this.grimmixLoader.buildRuntimeConfigs();
        this.grimmixLoader.coreLoad();
    }

    public void loadModMixins() {
        this.grimmixLoader.modLoad();
    }

    public void finish() {
        this.grimmixLoader.finish();
    }

    public Environment getEnvironment() {
        return this.side;
    }

    public File getMCLocation() {
        return this.mcLocation;
    }

    public File getModFolder() {
        return this.mcModFolder;
    }

    public File getDataFolder() {
        return this.dataFolder;
    }

    public File getConfigFolder() {
        return this.configFolder;
    }

    public LaunchClassLoader getClassLoader() {
        return classLoader;
    }

    public String getMCVersion() {
        return this.version;
    }

    public boolean isDevEnvironment() {
        if (this.obfuscated == null) {
            try {
                Field envField = this.coremodManager.getDeclaredField("deobfuscatedEnvironment");
                envField.setAccessible(true);
                this.obfuscated = envField.getBoolean(null);
            } catch (Exception ex) {
                Throwables.propagate(ex);
            }
        }

        return this.obfuscated.booleanValue();
    }

}
