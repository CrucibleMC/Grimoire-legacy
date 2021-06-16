package io.github.crucible.omniconfig.api.builders;

import java.util.function.Function;

import io.github.crucible.omniconfig.api.properties.IStringListProperty;
import io.github.crucible.omniconfig.core.properties.StringArrayParameter.Builder;

public interface IStringListPropertyBuilder extends IAbstractPropertyBuilder<IStringListProperty, IStringListPropertyBuilder> {

    public IStringListPropertyBuilder validValues(String... values);

    public IStringListPropertyBuilder validator(Function<String[], String[]> validator);

}
