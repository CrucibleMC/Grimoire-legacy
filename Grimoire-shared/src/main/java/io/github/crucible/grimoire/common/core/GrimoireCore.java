package io.github.crucible.grimoire.common.core;

import io.github.crucible.grimoire.common.api.grimmix.IGrimmix;
import io.github.crucible.grimoire.common.api.lib.Side;
import io.github.crucible.grimoire.common.integrations.IntegrationManager;
import io.github.crucible.grimoire.common.test.AnnotationConfigTest;
import io.github.crucible.grimoire.common.test.OmniconfigTest;
import io.github.crucible.omniconfig.OmniconfigCore;
import io.github.crucible.omniconfig.annotation.core.AnnotationConfigCore;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class GrimoireCore {
    public static final GrimoireCore INSTANCE = new GrimoireCore();
    public static final Logger logger = LogManager.getLogger("Grimoire");
    private static final LaunchClassLoader classLoader = (LaunchClassLoader) GrimoireCore.class.getClassLoader();

    private final GrimmixLoader grimmixLoader;
    private final IntegrationManager grimmixIntegrations;
    private File mcLocation;
    private File mcModFolder;
    private String version;
    private Side side;

    public GrimoireCore() {
        if (this.isDevEnvironment()) {
            System.setProperty("mixin.debug", "true");
            System.setProperty("mixin.hotSwap", "true");
            System.setProperty("mixin.env.disableRefMap", "true");
            System.setProperty("mixin.debug.export", "true");
            System.setProperty("mixin.checks", "true");
            System.setProperty("mixin.checks.interfaces", "true");
            System.setProperty("mixin.env", "true");
        }

        this.grimmixLoader = GrimmixLoader.INSTANCE;
        this.grimmixIntegrations = new IntegrationManager();
    }

    public void configure(File mcLocation, boolean obfuscated, String mcModFolder, String version, Side onSide) {
        this.side = onSide;
        this.version = version;
        this.mcLocation = mcLocation;
        this.mcModFolder = new File(this.mcLocation, mcModFolder);

        OmniconfigCore.logger.info("Initializing omniconfig core...");
    }

    public void init() {
        OmniconfigTest.INSTANCE.getClass(); // make it construct
        AnnotationConfigCore.INSTANCE.addAnnotationConfig(AnnotationConfigTest.class);

        this.grimmixLoader.scanForGrimmixes(classLoader, this.mcModFolder, new File(this.mcModFolder, this.version));

        this.grimmixLoader.construct();
        this.grimmixLoader.validate();
        this.grimmixLoader.coreLoad();
    }

    public void loadModMixins() {
        this.grimmixLoader.modLoad();
    }

    public void finish() {
        this.grimmixLoader.finish();
    }

    public IntegrationManager getGrimmixIntegrations() {
        return this.grimmixIntegrations;
    }

    public Side getEnvironment() {
        return this.side;
    }

    public File getMCLocation() {
        return this.mcLocation;
    }

    public File getMCModFolder() {
        return this.mcModFolder;
    }

    public boolean isDevEnvironment() {
        return Boolean.parseBoolean(System.getProperty("fml.isDevEnvironment"));
    }

}
