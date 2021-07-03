package io.github.crucible.omniconfig.api.builders;

import java.util.function.Function;

import io.github.crucible.omniconfig.api.lib.Perhaps;
import io.github.crucible.omniconfig.api.properties.IPerhapsProperty;

public interface IPerhapsPropertyBuilder extends IAbstractPropertyBuilder<IPerhapsProperty, IPerhapsPropertyBuilder> {

    /**
     * Specifies maximum percentage-based value this property will be allowed
     * to take.
     *
     * @param maxValue Maximal value of your choice.
     * @return This sub-builder instance.
     */
    IPerhapsPropertyBuilder max(double percent);

    /**
     * Specifies minimum percentage-based value this property will be allowed
     * to take.
     * @param minValue Minimal value of your choice.
     *
     * @return This sub-builder instance.
     */
    IPerhapsPropertyBuilder min(double percent);

    /**
     * Specifies both minimum and maximum percentage-based values thes property
     * will be allowed to take. Result of this invokation will be equal to doing
     * something like this:
     *
     * <pre>
     * propertyBuilder.min(-percent);
     * propertyBuilder.max(percent);
     * </pre>
     *
     * @param minMax Min-max bound of your choice.
     * @return This sub-builder instance.
     */
    IPerhapsPropertyBuilder minMax(double percent);

    /**
     * Supply validator function for this property.
     * See {@link IAbstractPropertyBuilder} class docs for more details on usage.
     *
     * @param validator Validator function.
     * @return This sub-builder instance
     * @see {@link IAbstractPropertyBuilder}
     */
    IPerhapsPropertyBuilder validator(Function<Perhaps, Perhaps> validator);

}
