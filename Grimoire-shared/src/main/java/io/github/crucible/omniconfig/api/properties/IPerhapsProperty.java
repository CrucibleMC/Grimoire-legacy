package io.github.crucible.omniconfig.api.properties;

import io.github.crucible.omniconfig.api.lib.Perhaps;

/**
 * Property that contains bounded {@link Perhaps} value.
 *
 * @author Aizistral
 */

public interface IPerhapsProperty extends IAbstractProperty {

    /**
     * @return Current value of this property.
     */
    public Perhaps getValue();

    /**
     * @return Maximum possible value this property can take.
     */
    public Perhaps getMax();

    /**
     * @return Minimum possible value this property can take.
     */
    public Perhaps getMin();

    /**
     * @return Default value of this property, as declared in config file.
     */
    public Perhaps getDefault();

}
