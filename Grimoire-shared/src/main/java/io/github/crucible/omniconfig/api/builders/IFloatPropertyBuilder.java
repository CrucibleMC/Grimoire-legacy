package io.github.crucible.omniconfig.api.builders;

import java.util.function.Function;

import io.github.crucible.omniconfig.api.properties.IFloatProperty;

public interface IFloatPropertyBuilder extends IAbstractPropertyBuilder<IFloatProperty, IFloatPropertyBuilder> {

    public IFloatPropertyBuilder max(float maxValue);

    public IFloatPropertyBuilder min(float minValue);

    public IFloatPropertyBuilder minMax(float minMax);

    public IFloatPropertyBuilder validator(Function<Float, Float> validator);

}
