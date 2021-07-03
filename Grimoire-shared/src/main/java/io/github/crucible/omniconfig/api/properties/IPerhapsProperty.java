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
    Perhaps getValue();

    /**
     * @return Maximum possible value this property can take.
     */
    Perhaps getMax();

    /**
     * @return Minimum possible value this property can take.
     */
    Perhaps getMin();

    /**
     * @return Default value of this property, as declared in config file.
     */
    Perhaps getDefault();

}
