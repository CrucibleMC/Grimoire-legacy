package io.github.crucible.grimoire.common.api;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixins;

import com.google.common.collect.ImmutableList;

import io.github.crucible.grimoire.common.GrimoireCore;
import io.github.crucible.grimoire.common.api.eventbus.CoreEventBus;
import io.github.crucible.grimoire.common.api.events.GrimoireEvent;
import io.github.crucible.grimoire.common.api.grimmix.IGrimmix;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.ICoreLoadEvent;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.IModLoadEvent;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.LoadingStage;
import io.github.crucible.grimoire.common.api.integration.IModIntegration;
import io.github.crucible.grimoire.common.api.integration.IModIntegrationRegistry;
import io.github.crucible.grimoire.common.api.lib.Environment;
import io.github.crucible.grimoire.common.api.mixin.ConfigurationType;
import io.github.crucible.grimoire.common.api.mixin.IMixinConfiguration;
import io.github.crucible.grimoire.common.core.GrimmixLoader;
import io.github.crucible.grimoire.common.core.MixinConfiguration;
import io.github.crucible.grimoire.common.core.VersionHandler;
import io.github.crucible.grimoire.common.integration.ModIntegrationRegistry;
import net.minecraft.launchwrapper.LaunchClassLoader;

/**
 * The heart and core of Grimoire API.
 *
 * @author Aizistral
 */

public class GrimoireAPI {

    private GrimoireAPI() {
        // Can't touch this
    }

    /**
     * General-purpose EventBus for posting any Grimoire-related events.<br/>
     * Any events posted via this bus must extend {@link GrimoireEvent}.
     */
    public static final CoreEventBus<GrimoireEvent> EVENT_BUS = CoreEventBus.create(GrimoireEvent.class, GrimoireConstants.MAIN_BUS_NAME);

    /**
     * If you <b>absolutely need</b> to register mixin configuration outside the
     * bounds of your controller's {@link ICoreLoadEvent} and {@link IModLoadEvent},
     * you should use this method to do so.<br/><br/>
     * For the love of gods, <b>never call {@link Mixins#addConfiguration(String)} directly.</b>
     *
     * @param classpath Path to configuration .json.
     * @param type Configuration type.
     * @return Registered {@link IMixinConfiguration} instance.
     * @see IMixinConfiguration
     * @see ConfigurationType
     */
    public static IMixinConfiguration registerMixinConfiguration(String classpath, ConfigurationType type) {
        return new MixinConfiguration(null, type, classpath, false);
    }

    /**
     * Same as {@link #registerMixinConfiguration(String, ConfigurationType)}, but registers
     * individual configuration for every path passed as an argument.
     *
     * @param type Configuration type.
     * @param classpaths List of paths to configuration .json's.
     * @return List of registered {@link IMixinConfiguration} instances.
     * @see #registerMixinConfiguration(String, ConfigurationType)
     */
    public static List<IMixinConfiguration> registerMixinConfigurations(ConfigurationType type, String... classpaths) {
        List<IMixinConfiguration> list = new ArrayList<>();
        for (String path : classpaths) {
            list.add(registerMixinConfiguration(path, type));
        }

        return list;
    }

    /**
     * Verify if grimmix with specified ID does exist and is currently loaded.
     *
     * @param id ID in question.
     * @return True if grimmix exists and is loaded, false otherwise.
     */
    public static boolean isGrimmixLoaded(String id) {
        for (IGrimmix grimmix : GrimmixLoader.INSTANCE.getAllActiveContainers()) {
            if (grimmix.getID().equals(id))
                return true;
        }

        return false;
    }

    /**
     * Try to locate grimmix with specified ID.
     *
     * @param id ID in question.
     * @return If such grimmix exists and is loaded, returns an {@link IGrimmix}
     * instance associated with that grimmix; otherwise resturns empty optional.
     */
    public static Optional<IGrimmix> getGrimmix(String id) {
        for (IGrimmix grimmix : GrimmixLoader.INSTANCE.getAllActiveContainers()) {
            if (grimmix.getID().equals(id))
                return Optional.of(grimmix);
        }

        return Optional.empty();
    }

    /**
     * @return List of all {@link IGrimmix} instances associated with currently
     * existing and valid grimmixes. Needless to say - unmodifiable.
     */
    public static List<IGrimmix> getLoadedGrimmixes() {
        return GrimmixLoader.INSTANCE.getAllActiveContainers();
    }

    /**
     * @return List of all valid {@link IMixinConfiguration} instances which are
     * not owned by any particular grimmix. Needless to say - unmodifiable.
     * @see #registerMixinConfiguration(String, ConfigurationType)
     */
    public static List<IMixinConfiguration> getUnclaimedConfigurations() {
        return MixinConfiguration.getUnclaimedConfigurations();
    }

    /**
     * @param ofType Configuration type returned configurations should have.
     * @return List of all valid {@link IMixinConfiguration} instances which have
     * exactly the same {@link ConfigurationType} as passed argument. Unmodifiable.
     * @see ConfigurationType
     */
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

    /**
     * @return List of all existing and valid {@link IMixinConfiguration} instances.
     * Needless to say - unmodifiable.
     */
    public static List<IMixinConfiguration> getAllConfigurations() {
        ImmutableList.Builder<IMixinConfiguration> builder = ImmutableList.builder();

        builder.addAll(getUnclaimedConfigurations());
        for (IGrimmix grimmix : getLoadedGrimmixes()) {
            builder.addAll(grimmix.getOwnedConfigurations());
        }

        return builder.build();
    }

    /**
     * Version-independent way to determine whether mod with particular Mod ID is currently
     * present. Be aware that at the time of grimmix lifecycle events no mods are discovered
     * and loaded yet, and trying to call this method at such time will always return false;
     * you should only call it from your <b><code>@Mod</code></b> class, ideally no earlier than at
     * the time of <b><code>FMLPreInitializationEvent</code></b>.
     *
     * @param modID ID of mod to verify existance of.
     * @return True if such mod exists and is loaded, false otherwise.
     */
    public static boolean isModLoaded(String modID) {
        if (GrimmixLoader.INSTANCE.getInternalStage() != LoadingStage.FINAL)
            return false;
        else
            return VersionHandler.instance().isModLoaded(modID);
    }

    /**
     * @return Registry for {@link IModIntegration} instances.
     */
    public static IModIntegrationRegistry getModIntegrationRegistry() {
        return ModIntegrationRegistry.INSTANCE;
    }

    /**
     * Serves as version-independent way to verify which sided Minecraft
     * environment we currently reside within.
     *
     * @return Current {@link Environment} we are in.
     * @see Environment
     */
    public static Environment getEnvironment() {
        return GrimoireCore.INSTANCE.getEnvironment();
    }

    /**
     * @return True if we currently reside within development environment,
     * false if we are in production environment.
     */
    public static boolean isDevEnvironment() {
        return GrimoireCore.INSTANCE.isDevEnvironment();
    }

    /**
     * @return True if we are in environment with obfuscated Minecraft
     * classes, false if classes are obfuscated. Normally it is expected
     * that returned value will always be opposite to that of
     * {@link #isDevEnvironment()}.
     */
    public static boolean isObfuscatedEnvironment() {
        return !GrimoireCore.INSTANCE.isDevEnvironment();
    }

    /**
     * @return {@link File} representing location of Minecraft directory itself.
     * In client production environment normally goes by the name
     * <code>.minecraft</code>.
     */
    public static File getMinecraftFolder() {
        return GrimoireCore.INSTANCE.getMCLocation();
    }

    /**
     * @return {@link File} representing location of mod folder within Minecraft
     * directory. Normally this will be <code>.minecraft/mods</code>.
     */
    public static File getModFolder() {
        return GrimoireCore.INSTANCE.getModFolder();
    }

    /**
     * @return Version-specific subfolder within mod folder within Minecraft
     * directory. If current Minecraft version is 1.7.10, it will normally be
     * <code>.minecraft/mods/1.7.10</code>; if version is 1.12.2, it will normally
     * be <code>.minecraft/mods/1.12.2</code>.
     */
    public static File getVersionedModFolder() {
        return GrimoireCore.INSTANCE.getVersionedModFolder();
    }

    /**
     * @return Folder used by Grimoire itself for storing some of runtime-generated
     * files, though its purpose may be extended beyond that. Normally this will be
     * <code>.minecraft/mcdata</code>
     */
    public static File getDataFolder() {
        return GrimoireCore.INSTANCE.getDataFolder();
    }

    /**
     * @return Instance of {@link LaunchClassLoader}.
     */
    public static LaunchClassLoader getLaunchClassloader() {
        return GrimoireCore.INSTANCE.getClassLoader();
    }

}