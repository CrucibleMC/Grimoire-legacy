package io.github.crucible.grimoire.common.integration;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;

import io.github.crucible.grimoire.common.GrimoireCore;
import io.github.crucible.grimoire.common.api.integration.IModIntegration;
import io.github.crucible.grimoire.common.api.integration.IModIntegrationRegistry;
import io.github.crucible.grimoire.common.api.integration.ModIntegrationContainer;

public class ModIntegrationRegistry implements IModIntegrationRegistry {
    public static final ModIntegrationRegistry INSTANCE = new ModIntegrationRegistry();

    private final List<Class<? extends ModIntegrationContainer<?>>> containerClasses = new ArrayList<>();
    private final List<ModIntegrationContainer<?>> containers = new ArrayList<>();
    private boolean initialized = false;

    private ModIntegrationRegistry() {
        // NO-OP
    }

    @Override
    public void registerIntegration(Class<? extends ModIntegrationContainer<?>> integrationClass) {
        if (this.initialized) {
            try {
                ModIntegrationContainer<?> container = integrationClass.getConstructor().newInstance();
                this.containers.add(container);
            } catch (Exception ex) {
                GrimoireCore.logger.fatal("Could not instantiate mod integration: " + integrationClass);
                Throwables.propagate(ex);
            }
        } else {
            this.containerClasses.add(integrationClass);
        }
    }

    public void init() {
        if (!this.initialized) {
            this.initialized = true;

            for (Class<? extends ModIntegrationContainer<?>> integrationClass : this.containerClasses) {
                try {
                    ModIntegrationContainer<?> container = integrationClass.getConstructor().newInstance();
                    this.containers.add(container);
                } catch (Throwable ex) {
                    GrimoireCore.logger.fatal("Could not instantiate mod integration: " + integrationClass);
                    Throwables.propagate(ex);
                }
            }

            this.containerClasses.clear();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IModIntegration> T getIntegration(Class<T> integrationClass) {
        for (ModIntegrationContainer<?> integration : this.containers) {
            if (integration.getIntegrationClass().equals(integrationClass))
                return (T) integration.getIntegration();
        }

        return null;
    }

    @Override
    public boolean isInitialized() {
        return this.initialized;
    }

    @Override
    public List<IModIntegration> getAllIntegrations() {
        ImmutableList.Builder<IModIntegration> listBuilder = ImmutableList.builder();

        for (ModIntegrationContainer<? extends IModIntegration> container : this.containers) {
            listBuilder.add(container.getIntegration());
        }

        return listBuilder.build();
    }

}

