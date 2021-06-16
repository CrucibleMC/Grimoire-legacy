package io.github.crucible.omniconfig.api.builders;

import java.util.function.Function;

import io.github.crucible.omniconfig.api.properties.IIntegerProperty;

public interface IIntegerPropertyBuilder extends IAbstractPropertyBuilder<IIntegerProperty, IIntegerPropertyBuilder> {

    public IIntegerPropertyBuilder max(int maxValue);

    public IIntegerPropertyBuilder min(int minValue);

    public IIntegerPropertyBuilder minMax(int minMax);

    public IIntegerPropertyBuilder validator(Function<Integer, Integer> validator);

}
