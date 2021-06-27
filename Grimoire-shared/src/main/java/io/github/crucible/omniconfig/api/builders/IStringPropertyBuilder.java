package io.github.crucible.omniconfig.api.builders;

import java.util.function.Function;

import io.github.crucible.omniconfig.api.properties.IStringProperty;

public interface IStringPropertyBuilder extends IAbstractPropertyBuilder<IStringProperty, IStringPropertyBuilder> {

    public IStringPropertyBuilder validValues(String... values);

    public IStringPropertyBuilder validator(Function<String, String> validator);

}
