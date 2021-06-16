package io.github.crucible.omniconfig.api.builders;

import java.util.function.Consumer;

import io.github.crucible.omniconfig.api.properties.IAbstractProperty;
import io.github.crucible.omniconfig.core.properties.AbstractParameter;

public interface IAbstractPropertyBuilder<E extends IAbstractProperty, T extends IAbstractPropertyBuilder<E, T>> {

    public T comment(String comment);

    public T sync(boolean isSyncable);

    public T sync();

    public T uponLoad(Consumer<E> consumer, boolean invokeOnFirstLoad);

    public T uponLoad(Consumer<E> consumer);

    public E build();

}
