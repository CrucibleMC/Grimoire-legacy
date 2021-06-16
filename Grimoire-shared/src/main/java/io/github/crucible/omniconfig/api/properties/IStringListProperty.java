package io.github.crucible.omniconfig.api.properties;

import java.util.List;

public interface IStringListProperty extends IAbstractProperty {

    public List<String> getDefault();

    public List<String> getValue();

    public List<String> getValidValues();

    public String[] getValueAsArray();

}
