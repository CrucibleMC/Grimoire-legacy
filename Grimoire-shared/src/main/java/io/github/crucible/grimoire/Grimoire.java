package io.github.crucible.grimoire;

import io.github.crucible.grimoire.core.GrimmixLoader;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import java.io.File;

public class Grimoire {
    public static final Logger logger = LogManager.getLogger("Grimoire");
    static LaunchClassLoader classLoader = (LaunchClassLoader) Grimoire.class.getClassLoader();
    static GrimmixLoader grimmixLoader;

    static void construct() {
        if (isDevEnvironment()) {
            System.setProperty("mixin.debug", "true");
            System.setProperty("mixin.hotSwap", "true");
            System.setProperty("mixin.env.disableRefMap", "true");
            System.setProperty("mixin.debug.export", "true");
            System.setProperty("mixin.checks", "true");
            System.setProperty("mixin.checks.interfaces", "true");
            System.setProperty("mixin.env", "true");
        }
        grimmixLoader = GrimmixLoader.INSTANCE;
    }

    static void configure(File mcLocation, boolean obfuscated, String mcModFolder, String version) {

        grimmixLoader.scanForGrimmixes(classLoader,
                new File(mcLocation, mcModFolder),
                new File(mcLocation, mcModFolder + File.separator + version));

        grimmixLoader.construct();
        grimmixLoader.validate();
        grimmixLoader.coreLoad();
    }

    public static boolean isDevEnvironment() {
        return Boolean.parseBoolean(System.getProperty("fml.isDevEnvironment"));
    }
}
