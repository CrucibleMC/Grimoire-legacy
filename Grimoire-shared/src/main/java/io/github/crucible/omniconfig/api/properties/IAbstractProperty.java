package io.github.crucible.omniconfig.api.properties;

public interface IAbstractProperty {

    public String getID();

    public String getCategory();

    public String getName();

    public String getComment();

    public boolean isSynchronized();

}
