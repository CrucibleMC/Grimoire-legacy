package io.github.crucible.omniconfig.api.builders;

import java.util.function.Function;

import io.github.crucible.omniconfig.api.properties.IStringProperty;
import io.github.crucible.omniconfig.core.properties.StringParameter.Builder;

public interface IStringPropertyBuilder extends IAbstractPropertyBuilder<IStringProperty, IStringPropertyBuilder> {

    public IStringPropertyBuilder validValues(String... values);

    public IStringPropertyBuilder validator(Function<String, String> validator);

}
