package io.github.crucible.omniconfig.api.builders;

import java.util.function.Function;

import io.github.crucible.omniconfig.api.properties.IStringListProperty;

public interface IStringListPropertyBuilder extends IAbstractPropertyBuilder<IStringListProperty, IStringListPropertyBuilder> {

    public IStringListPropertyBuilder validValues(String... values);

    public IStringListPropertyBuilder validator(Function<String[], String[]> validator);

}
