package io.github.crucible.grimoire.common.api;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableList;

import io.github.crucible.grimoire.common.GrimoireCore;
import io.github.crucible.grimoire.common.api.eventbus.CoreEventBus;
import io.github.crucible.grimoire.common.api.events.GrimoireEvent;
import io.github.crucible.grimoire.common.api.grimmix.IGrimmix;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.LoadingStage;
import io.github.crucible.grimoire.common.api.integration.IModIntegrationRegistry;
import io.github.crucible.grimoire.common.api.lib.Environment;
import io.github.crucible.grimoire.common.api.mixin.ConfigurationType;
import io.github.crucible.grimoire.common.api.mixin.IMixinConfiguration;
import io.github.crucible.grimoire.common.core.GrimmixLoader;
import io.github.crucible.grimoire.common.core.MixinConfiguration;
import io.github.crucible.grimoire.common.core.VersionHandler;
import io.github.crucible.grimoire.common.integration.ModIntegrationRegistry;
import net.minecraft.launchwrapper.LaunchClassLoader;

public class GrimoireAPI {

    /**
     * General-purpose EventBus for posting any Grimoire-related events.<br/>
     * Any events posted via this bus must extend {@link GrimoireEvent}.
     */
    public static final CoreEventBus<GrimoireEvent> EVENT_BUS = CoreEventBus.create(GrimoireEvent.class, GrimoireConstants.MAIN_BUS_NAME);

    private GrimoireAPI() {
        // Can't touch this
    }

    public static IMixinConfiguration registerMixinConfiguration(String classpath, ConfigurationType type) {
        return new MixinConfiguration(null, type, classpath, false);
    }

    public static List<IMixinConfiguration> registerMixinConfigurations(ConfigurationType type, String... classpaths) {
        List<IMixinConfiguration> list = new ArrayList<>();
        for (String path : classpaths) {
            list.add(registerMixinConfiguration(path, type));
        }

        return list;
    }

    public static boolean isGrimmixLoaded(String id) {
        for (IGrimmix grimmix : GrimmixLoader.INSTANCE.getAllActiveContainers()) {
            if (grimmix.getID().equals(id))
                return true;
        }

        return false;
    }

    @Nullable
    public static IGrimmix getGrimmix(String id) {
        for (IGrimmix grimmix : GrimmixLoader.INSTANCE.getAllActiveContainers()) {
            if (grimmix.getID().equals(id))
                return grimmix;
        }

        return null;
    }

    public static List<IGrimmix> getLoadedGrimmixes() {
        return GrimmixLoader.INSTANCE.getAllActiveContainers();
    }

    public static List<IMixinConfiguration> getUnclaimedConfigurations() {
        return MixinConfiguration.getUnclaimedConfigurations();
    }

    public static List<IMixinConfiguration> getAllConfigurations(ConfigurationType ofType) {
        ImmutableList.Builder<IMixinConfiguration> builder = ImmutableList.builder();

        getUnclaimedConfigurations().forEach(configuration -> {
            if (configuration.getConfigurationType() == ofType) {
                builder.add(configuration);
            }
        });

        for (IGrimmix grimmix : getLoadedGrimmixes()) {
            grimmix.getOwnedConfigurations().forEach(configuration -> {
                if (configuration.getConfigurationType() == ofType) {
                    builder.add(configuration);
                }
            });
        }

        return builder.build();
    }

    public static List<IMixinConfiguration> getAllConfigurations() {
        ImmutableList.Builder<IMixinConfiguration> builder = ImmutableList.builder();

        builder.addAll(getUnclaimedConfigurations());
        for (IGrimmix grimmix : getLoadedGrimmixes()) {
            builder.addAll(grimmix.getOwnedConfigurations());
        }

        return builder.build();
    }

    public static boolean isModLoaded(String modID) {
        if (GrimmixLoader.INSTANCE.getInternalStage() != LoadingStage.FINAL)
            return false;
        else
            return VersionHandler.instance().isModLoaded(modID);
    }

    public static IModIntegrationRegistry getModIntegrationRegistry() {
        return ModIntegrationRegistry.INSTANCE;
    }

    public static Environment getEnvironment() {
        return GrimoireCore.INSTANCE.getEnvironment();
    }

    public static boolean isDevEnvironment() {
        return GrimoireCore.INSTANCE.isDevEnvironment();
    }

    public static boolean isObfuscatedEnvironment() {
        return !GrimoireCore.INSTANCE.isDevEnvironment();
    }

    public static File getMinecraftFolder() {
        return GrimoireCore.INSTANCE.getMCLocation();
    }

    public static File getModFolder() {
        return GrimoireCore.INSTANCE.getModFolder();
    }

    public static File getVersionedModFolder() {
        return GrimoireCore.INSTANCE.getVersionedModFolder();
    }

    public static File getDataFolder() {
        return GrimoireCore.INSTANCE.getDataFolder();
    }

    public static LaunchClassLoader getLaunchClassloader() {
        return GrimoireCore.INSTANCE.getClassLoader();
    }

}