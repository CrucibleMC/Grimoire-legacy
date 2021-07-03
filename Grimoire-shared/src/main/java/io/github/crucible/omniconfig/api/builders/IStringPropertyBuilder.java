package io.github.crucible.omniconfig.api.builders;

import java.util.function.Function;

import io.github.crucible.omniconfig.api.properties.IStringProperty;

public interface IStringPropertyBuilder extends IAbstractPropertyBuilder<IStringProperty, IStringPropertyBuilder> {

    /**
     * Specifies list of valid values strings this propertie's value must match
     * one of.
     *
     * @param values Valid values.
     * @return This sub-builder instance.
     */
    IStringPropertyBuilder validValues(String... values);

    /**
     * Supply validator function for this property.
     * See {@link IAbstractPropertyBuilder} class docs for more details on usage.
     *
     * @param validator Validator function.
     * @return This sub-builder instance
     * @see {@link IAbstractPropertyBuilder}
     */
    IStringPropertyBuilder validator(Function<String, String> validator);

}
