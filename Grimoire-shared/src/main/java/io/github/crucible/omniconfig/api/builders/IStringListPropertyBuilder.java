package io.github.crucible.omniconfig.api.builders;

import java.util.function.Function;

import io.github.crucible.omniconfig.api.properties.IStringListProperty;

public interface IStringListPropertyBuilder extends IAbstractPropertyBuilder<IStringListProperty, IStringListPropertyBuilder> {

    /**
     * Specifies list of valid values for this property. If invoked, every string
     * in string list this property will contain must exactly match one of supplied values.
     *
     * @param values Valid values.
     * @return This sub-builder instance.
     */
    IStringListPropertyBuilder validValues(String... values);

    /**
     * Supply validator function for this property.
     * See {@link IAbstractPropertyBuilder} class docs for more details on usage.
     *
     * @param validator Validator function.
     * @return This sub-builder instance
     * @see {@link IAbstractPropertyBuilder}
     */
    IStringListPropertyBuilder validator(Function<String[], String[]> validator);

}
