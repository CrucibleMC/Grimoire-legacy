package io.github.crucible.omniconfig.api.builders;

import java.util.function.Function;

import io.github.crucible.omniconfig.api.properties.IDoubleProperty;
import io.github.crucible.omniconfig.core.properties.DoubleParameter.Builder;

public interface IDoublePropertyBuilder extends IAbstractPropertyBuilder<IDoubleProperty, IDoublePropertyBuilder> {

    public IDoublePropertyBuilder max(double maxValue);

    public IDoublePropertyBuilder min(double minValue);

    public IDoublePropertyBuilder minMax(double minMax);

    public IDoublePropertyBuilder validator(Function<Double, Double> validator);

}
