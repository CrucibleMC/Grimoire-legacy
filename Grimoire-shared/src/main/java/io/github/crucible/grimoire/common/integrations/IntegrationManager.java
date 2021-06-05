package io.github.crucible.grimoire.common.integrations;

import io.github.crucible.grimoire.common.core.GrimoireCore;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

public class IntegrationManager {
    private List<IIntegration<?>> integrations = new ArrayList<>();

    public void registerIntegration(IIntegration<?> integration) {
        integrations.add(Objects.requireNonNull(integration));
    }

    public void init() {
        for (IIntegration<?> integration : integrations) {
            try {
                integration.initIntegration();
            } catch (Throwable e) {
                GrimoireCore.logger.error("Unable to initialize integration \"{}\"\nRemoving it.", integration.getClass().getName());
                integrations.remove(integration);
                e.printStackTrace();
            }
        }
    }


    @SuppressWarnings("unchecked")
    public <T> T getIntegrationFrom(Class<IIntegration<T>> clazz) {
        for (IIntegration<?> integration : integrations) {
            if (integration.getClass().equals(clazz))
                return (T) integration.getIntegration();
        }
        throw new NoSuchElementException("Unable to find any registered integration with class the following class: " + clazz.getName());
    }

}

