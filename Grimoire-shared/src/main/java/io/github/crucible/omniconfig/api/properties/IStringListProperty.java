package io.github.crucible.omniconfig.api.properties;

import java.util.List;

/**
 * Property that contains a list of {@link String}s.
 *
 * @author Aizistral
 */

public interface IStringListProperty extends IAbstractProperty {

    /**
     * @return Current value of this property.
     */
    public List<String> getValue();

    /**
     * @return Default value of this property, as declared in config file.
     */
    public List<String> getDefault();

    /**
     * @return List of valid {@link String}s this propertie's value may contain.
     * If valid values are not restricted, an empty list is returned instead.
     */
    public List<String> getValidValues();

    /**
     * @return Basically, a result of {@link #getValue()}, but in a form or raw
     * array.
     */
    public String[] getValueAsArray();

}
