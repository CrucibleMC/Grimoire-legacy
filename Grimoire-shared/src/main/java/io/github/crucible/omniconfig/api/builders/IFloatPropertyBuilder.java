package io.github.crucible.omniconfig.api.builders;

import java.util.function.Function;

import io.github.crucible.omniconfig.api.properties.IFloatProperty;

public interface IFloatPropertyBuilder extends IAbstractPropertyBuilder<IFloatProperty, IFloatPropertyBuilder> {

    /**
     * Specifies maximum value this property will be allowed to take.
     *
     * @param maxValue Maximal value of your choice.
     * @return This sub-builder instance.
     */
    IFloatPropertyBuilder max(float maxValue);

    /**
     * Specifies minimum value this property will be allowed to take.
     *
     * @param minValue Minimal value of your choice.
     * @return This sub-builder instance.
     */
    IFloatPropertyBuilder min(float minValue);

    /**
     * Specifies both minimum and maximum values thes property will
     * be allowed to take. Result of this invokation will be equal
     * to doing something like this:
     *
     * <pre>
     * propertyBuilder.min(-minMax);
     * propertyBuilder.max(minMax);
     * </pre>
     *
     * @param minMax Min-max bound of your choice.
     * @return This sub-builder instance.
     */
    IFloatPropertyBuilder minMax(float minMax);

    /**
     * Supply validator function for this property.
     * See {@link IAbstractPropertyBuilder} class docs for more details on usage.
     *
     * @param validator Validator function.
     * @return This sub-builder instance
     * @see {@link IAbstractPropertyBuilder}
     */
    IFloatPropertyBuilder validator(Function<Float, Float> validator);

}
