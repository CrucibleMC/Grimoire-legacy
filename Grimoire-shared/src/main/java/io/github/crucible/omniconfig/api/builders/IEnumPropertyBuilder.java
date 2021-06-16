package io.github.crucible.omniconfig.api.builders;

import java.util.function.Function;

import io.github.crucible.omniconfig.api.properties.IEnumProperty;

public interface IEnumPropertyBuilder<T extends Enum<T>> extends IAbstractPropertyBuilder<IEnumProperty<T>, IEnumPropertyBuilder<T>> {

    public IEnumPropertyBuilder<T> validValues(T... values);

    public IEnumPropertyBuilder<T> validator(Function<T, T> validator);

}