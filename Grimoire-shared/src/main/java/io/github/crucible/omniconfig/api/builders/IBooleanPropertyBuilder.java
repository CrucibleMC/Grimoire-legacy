package io.github.crucible.omniconfig.api.builders;

import java.util.function.Function;

import io.github.crucible.omniconfig.api.properties.IBooleanProperty;

/**
 * Specialized sub-builder for {@link IBooleanProperty}.
 *
 * @author Aizistral
 */

public interface IBooleanPropertyBuilder extends IAbstractPropertyBuilder<IBooleanProperty, IBooleanPropertyBuilder> {

    /**
     * Supply validator function for this property.
     * See {@link IAbstractPropertyBuilder} class docs for more details on usage.
     *
     * @param validator Validator function.
     * @return This sub-builder instance
     * @see {@link IAbstractPropertyBuilder}
     */
    public IBooleanPropertyBuilder validator(Function<Boolean, Boolean> validator);

}
