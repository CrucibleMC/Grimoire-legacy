package io.github.crucible.grimoire.common.api.events.core;

import java.util.List;

import io.github.crucible.grimoire.common.api.events.core.CoreEventBus.CoreEventHandler;

public interface IExceptionHandler<T extends CoreEvent> {

    public void handleException(CoreEventBus<T> bus, T event, Throwable exception);

}
