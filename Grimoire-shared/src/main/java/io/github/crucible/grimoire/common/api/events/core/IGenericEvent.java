package io.github.crucible.grimoire.common.api.events.core;

import java.lang.reflect.Type;

public interface IGenericEvent<T> {

    public Type getGenericType();

}
