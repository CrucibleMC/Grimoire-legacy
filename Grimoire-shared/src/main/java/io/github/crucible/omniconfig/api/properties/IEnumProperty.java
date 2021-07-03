package io.github.crucible.omniconfig.api.properties;

import java.util.List;

/**
 * Property that contains enum constant of generic type.
 *
 * @param <T> Type of specific {@link Enum} this property embeds.
 * @author Aizistral
 */

public interface IEnumProperty<T extends Enum<T>> extends IAbstractProperty {

    /**
     * @return Current value this property has.
     */
    T getValue();

    /**
     * @return Default value of this property, as declared in config file.
     */
    T getDefault();

    /**
     * @return List of valid values this property can take, if any were declared.
     * If not, returned list will contain all existing enum constants from
     * appropriate class.
     */
    List<T> getValidValues();

}
