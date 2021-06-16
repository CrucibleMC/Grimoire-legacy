package io.github.crucible.omniconfig.api.properties;

import java.util.List;

public interface IEnumProperty<T extends Enum<T>> extends IAbstractProperty {

    public T getValue();

    public T getDefault();

    public List<T> getValidValues();

}
