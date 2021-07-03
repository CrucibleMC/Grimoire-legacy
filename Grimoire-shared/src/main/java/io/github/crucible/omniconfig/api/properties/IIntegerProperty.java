package io.github.crucible.omniconfig.api.properties;

/**
 * Property that contains bounded primitive <code>int</code> value.
 *
 * @author Aizistral
 */

public interface IIntegerProperty extends IAbstractProperty {

    /**
     * @return Current value of this property.
     */
    int getValue();

    /**
     * @return Maximum possible value this property can take.
     */
    int getMax();

    /**
     * @return Minimum possible value this property can take.
     */
    int getMin();

    /**
     * @return Default value of this property, as declared in config file.
     */
    int getDefault();

}