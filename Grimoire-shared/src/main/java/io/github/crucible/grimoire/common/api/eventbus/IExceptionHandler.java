package io.github.crucible.grimoire.common.api.eventbus;

import java.util.List;

import io.github.crucible.grimoire.common.api.eventbus.CoreEventBus.CoreEventHandler;

public interface IExceptionHandler<T extends CoreEvent> {

    public void handleException(CoreEventBus<T> bus, T event, Throwable exception);

}
