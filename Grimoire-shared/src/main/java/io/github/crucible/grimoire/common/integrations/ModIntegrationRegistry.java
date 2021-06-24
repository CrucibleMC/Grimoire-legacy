package io.github.crucible.grimoire.common.integrations;

import io.github.crucible.grimoire.common.core.GrimoireCore;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import com.google.common.base.Throwables;

// TODO Make this a singleton instead of static class, API exposure
public class ModIntegrationRegistry {
    private static final List<Class<? extends ModIntegrationContainer<?>>> containerClasses = new ArrayList<>();
    private static final List<ModIntegrationContainer<?>> containers = new ArrayList<>();
    private static boolean initialized = false;

    private ModIntegrationRegistry() {
        // NO-OP
    }

    public static void registerIntegration(Class<? extends ModIntegrationContainer<?>> integrationClass) {
        if (initialized) {
            try {
                ModIntegrationContainer<?> container = integrationClass.getConstructor().newInstance();
                containers.add(container);
            } catch (Exception ex) {
                GrimoireCore.logger.fatal("Could not instantiate mod integration: " + integrationClass);
                Throwables.propagate(ex);
            }
        } else {
            containerClasses.add(integrationClass);
        }
    }

    public static void init() {
        if (!initialized) {
            initialized = true;

            for (Class<? extends ModIntegrationContainer<?>> integrationClass : containerClasses) {
                try {
                    ModIntegrationContainer<?> container = integrationClass.getConstructor().newInstance();
                    containers.add(container);
                } catch (Throwable ex) {
                    GrimoireCore.logger.fatal("Could not instantiate mod integration: " + integrationClass);
                    Throwables.propagate(ex);
                }
            }

            containerClasses.clear();
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends IModIntegration> T getIntegration(Class<T> integrationClass) {
        for (ModIntegrationContainer<?> integration : containers) {
            if (integration.getIntegrationClass().equals(integrationClass))
                return (T) integration.getIntegration();
        }

        return null;
    }

    public static boolean isInitialized() {
        return initialized;
    }

}

