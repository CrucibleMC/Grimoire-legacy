package io.github.crucible.grimoire.common.api;

import com.google.common.collect.ImmutableList;
import io.github.crucible.grimoire.common.api.configurations.IMixinConfiguration;
import io.github.crucible.grimoire.common.api.configurations.IMixinConfiguration.ConfigurationType;
import io.github.crucible.grimoire.common.api.eventbus.CoreEvent;
import io.github.crucible.grimoire.common.api.eventbus.CoreEventBus;
import io.github.crucible.grimoire.common.api.events.GrimoireEvent;
import io.github.crucible.grimoire.common.api.grimmix.IGrimmix;
import io.github.crucible.grimoire.common.api.integration.IModIntegrationRegistry;
import io.github.crucible.grimoire.common.core.GrimmixContainer;
import io.github.crucible.grimoire.common.core.GrimmixLoader;
import io.github.crucible.grimoire.common.core.MixinConfiguration;
import io.github.crucible.grimoire.common.integration.ModIntegrationRegistry;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

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

    public static boolean isGrimmixLoaded(String name) {
        for (IGrimmix grimmix : GrimmixLoader.INSTANCE.getAllActiveContainers()) {
            if (grimmix.getName().equals(name))
                return true;
        }

        return false;
    }

    @Nullable
    public static IGrimmix getGrimmix(String name) {
        for (IGrimmix grimmix : GrimmixLoader.INSTANCE.getAllActiveContainers()) {
            if (grimmix.getName().equals(name))
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

    public static List<IMixinConfiguration> getAllConfigurations() {
        ImmutableList.Builder<IMixinConfiguration> builder = ImmutableList.builder();

        builder.addAll(getUnclaimedConfigurations());
        for (IGrimmix grimmix : getLoadedGrimmixes()) {
            builder.addAll(grimmix.getOwnedConfigurations());
        }

        return builder.build();
    }

    public static IModIntegrationRegistry getModIntegrationRegistry() {
        return ModIntegrationRegistry.INSTANCE;
    }


}
