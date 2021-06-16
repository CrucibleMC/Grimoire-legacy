package io.github.crucible.omniconfig.api.properties;

import java.util.List;

public interface IStringProperty extends IAbstractProperty {

    public String getValue();

    public List<String> getValidValues();

}
