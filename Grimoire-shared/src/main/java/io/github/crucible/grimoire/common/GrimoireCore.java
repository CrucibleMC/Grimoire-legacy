package io.github.crucible.grimoire.common;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Throwables;

import io.github.crucible.grimoire.common.api.GrimoireAPI;
import io.github.crucible.grimoire.common.api.lib.Environment;
import io.github.crucible.grimoire.common.api.mixin.IMixinConfiguration;
import io.github.crucible.grimoire.common.config.GrimoireConfig;
import io.github.crucible.grimoire.common.core.GrimmixLoader;
import io.github.crucible.omniconfig.OmniconfigCore;
import io.github.crucible.omniconfig.api.OmniconfigAPI;
import net.minecraft.launchwrapper.LaunchClassLoader;

public class GrimoireCore {
    private static final LaunchClassLoader classLoader = (LaunchClassLoader) GrimoireCore.class.getClassLoader();
    public static final Logger logger = LogManager.getLogger("Grimoire");
    public static final GrimoireCore INSTANCE = new GrimoireCore();
    public static final String VERSION = "@VERSION@";

    private List<String> forcedFilenames = new ArrayList<String>();;
    private final GrimmixLoader grimmixLoader;
    private File mcLocation;
    private File modFolder;
    private File versionedModFolder;
    private File configFolder;
    private File dataFolder;
    private String mcVersion;
    private Environment side;
    private Boolean obfuscated = null;
    private Class<?> coremodManager = null;

    public GrimoireCore() {
        this.grimmixLoader = GrimmixLoader.INSTANCE;
        logger.info("I log, hereby, I exist.");
    }

    public void setCoremodManager(Class<?> coreModManager) {
        this.coremodManager = coreModManager;

        if (this.isDevEnvironment()) {
            logger.info("Loading within dev environment...");

            System.setProperty("mixin.debug", "true");
            System.setProperty("mixin.hotSwap", "true");
            System.setProperty("mixin.env.disableRefMap", "true"); // TODO Remapping support?
            System.setProperty("mixin.debug.export", "true");
            System.setProperty("mixin.debug.countInjections", "true");
            System.setProperty("mixin.checks", "true");
            System.setProperty("mixin.checks.interfaces", "true");
            System.setProperty("mixin.env", "true");
        } else {
            logger.info("Loading within production environment...");
        }
    }

    public void configure(File mcLocation, String mcModFolder, String mcVersion, Environment onSide) {
        this.side = onSide;
        this.mcVersion = mcVersion;
        this.mcLocation = mcLocation;
        this.modFolder = new File(this.mcLocation, mcModFolder);
        this.versionedModFolder = new File(this.modFolder, this.mcVersion);
        this.configFolder = new File(this.mcLocation, "config");
        this.dataFolder = new File(this.mcLocation, "mcdata");

        this.configFolder.mkdirs();
        this.dataFolder.mkdirs();

        GrimoireAPI.EVENT_BUS.getClass(); // Make it happen

        OmniconfigCore.logger.info("Initializing omniconfig core...");
        OmniconfigAPI.registerAnnotationConfig(GrimoireConfig.class);
    }

    public void init() {
        this.grimmixLoader.scanForGrimmixes(classLoader, this.modFolder, this.versionedModFolder);

        this.grimmixLoader.construct();
        this.grimmixLoader.validate();
        this.grimmixLoader.buildRuntimeConfigs();

        this.grimmixLoader.prepareCoreConfigs();
        this.grimmixLoader.loadCoreConfigs();
    }

    public List<IMixinConfiguration> prepareModConfigs() {
        this.grimmixLoader.prepareModConfigs();
        return this.grimmixLoader.getPreparedConfigs();
    }

    public void loadModConfigs() {
        this.grimmixLoader.loadModConfigs();
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
        return this.modFolder;
    }

    public File getVersionedModFolder() {
        return this.versionedModFolder;
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
        return this.mcVersion;
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

    public List<String> getForcedFilenames() {
        return this.forcedFilenames;
    }

}
