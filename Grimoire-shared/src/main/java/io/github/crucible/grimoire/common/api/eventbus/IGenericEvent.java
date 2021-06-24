package io.github.crucible.grimoire.common.api.eventbus;

import java.lang.reflect.Type;

public interface IGenericEvent<T> {

    public Type getGenericType();

}
