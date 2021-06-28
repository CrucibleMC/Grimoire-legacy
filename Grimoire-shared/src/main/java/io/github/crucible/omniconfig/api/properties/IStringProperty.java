package io.github.crucible.omniconfig.api.properties;

import java.util.List;

/**
 * Property that contains single {@link String} value.
 *
 * @author Aizistral
 */

public interface IStringProperty extends IAbstractProperty {

    /**
     * @return Current value of this property.
     */
    public String getValue();

    /**
     * @return Default value of this property, as declared in config file.
     */
    public String getDefault();

    /**
     * @return List of valid {@link String}s that this propertie's value may
     * be one of. If valid values are not restricted, an empty list is returned instead.
     */
    public List<String> getValidValues();

}
