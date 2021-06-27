package io.github.crucible.omniconfig.api;

import java.io.File;

import io.github.crucible.grimoire.common.GrimoireCore;
import io.github.crucible.omniconfig.OmniconfigCore;
import io.github.crucible.omniconfig.api.annotation.IAnnotationConfigRegistry;
import io.github.crucible.omniconfig.api.builders.IOmniconfigBuilder;
import io.github.crucible.omniconfig.api.core.IOmniconfigRegistry;
import io.github.crucible.omniconfig.api.core.SidedConfigType;
import io.github.crucible.omniconfig.api.lib.Version;
import io.github.crucible.omniconfig.core.Omniconfig;
import io.github.crucible.omniconfig.core.OmniconfigRegistry;
import io.github.crucible.omniconfig.gconfig.AnnotationConfigCore;
import net.minecraft.launchwrapper.LaunchClassLoader;

public class OmniconfigAPI {

    public static IOmniconfigBuilder configBuilder(String fileName) {
        return Omniconfig.builder(OmniconfigCore.INSTANCE.sanitizeName(fileName), new Version("1.0.0"), true, SidedConfigType.COMMON);
    }

    public static IOmniconfigBuilder configBuilder(String fileName, Version version) {
        return Omniconfig.builder(OmniconfigCore.INSTANCE.sanitizeName(fileName), version, true, SidedConfigType.COMMON);
    }

    public static IOmniconfigBuilder configBuilder(String fileName, Version version, SidedConfigType sidedType) {
        return Omniconfig.builder(OmniconfigCore.INSTANCE.sanitizeName(fileName), version, true, sidedType);
    }

    public static void registerAnnotationConfig(Class<?> annotationConfigClass) {
        AnnotationConfigCore.INSTANCE.addAnnotationConfig(annotationConfigClass);
    }

    public static IOmniconfigRegistry getOmniconfigRegistry() {
        return OmniconfigRegistry.INSTANCE;
    }

    public static IAnnotationConfigRegistry getAnnotationConfigRegistry() {
        return AnnotationConfigCore.INSTANCE;
    }

    public static File getConfigFolder() {
        return OmniconfigCore.CONFIG_DIR;
    }

}