package io.github.crucible.omniconfig.api.properties;

/**
 * Property that contains bounded primitive <code>double</code> value.
 *
 * @author Aizistral
 */

public interface IDoubleProperty extends IAbstractProperty {

    /**
     * @return Current value of this property.
     */
    public double getValue();

    /**
     * @return Maximum possible value this property can take.
     */
    public double getMax();

    /**
     * @return Minimum possible value this property can take.
     */
    public double getMin();

    /**
     * @return Default value of this property, as declared in config file.
     */
    public double getDefault();

}
