package io.github.crucible.grimoire.common.api.eventbus;

public interface IExceptionHandler<T extends CoreEvent> {

    void handleException(CoreEventBus<T> bus, T event, Throwable exception);

}
