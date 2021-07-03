package io.github.crucible.omniconfig.api.properties;

/**
 * Property that contains bounded primitive <code>float</code> value.
 *
 * @author Aizistral
 */

public interface IFloatProperty extends IAbstractProperty {

    /**
     * @return Current value of this property.
     */
    float getValue();

    /**
     * @return Maximum possible value this property can take.
     */
    float getMax();

    /**
     * @return Minimum possible value this property can take.
     */
    float getMin();

    /**
     * @return Default value of this property, as declared in config file.
     */
    float getDefault();

}