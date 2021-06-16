package io.github.crucible.omniconfig.api.builders;

import java.util.function.Function;

import io.github.crucible.omniconfig.api.properties.IBooleanProperty;
import io.github.crucible.omniconfig.core.properties.BooleanParameter.Builder;

public interface IBooleanPropertyBuilder extends IAbstractPropertyBuilder<IBooleanProperty, IBooleanPropertyBuilder> {

    public IBooleanPropertyBuilder validator(Function<Boolean, Boolean> validator);

}
