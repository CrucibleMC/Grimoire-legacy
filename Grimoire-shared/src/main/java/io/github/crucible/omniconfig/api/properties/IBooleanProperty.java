package io.github.crucible.omniconfig.api.properties;

/**
 * Property that contains primitive <code>boolean</code> value.
 *
 * @author Aizistral
 */

public interface IBooleanProperty extends IAbstractProperty {

    /**
     * @return Current value of this property.
     */
    public boolean getValue();

    /**
     * @return Default value of this property, as declared in config file.
     */
    public boolean getDefault();

}
