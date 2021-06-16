package io.github.crucible.omniconfig.api.properties;

public interface IIntegerProperty extends IAbstractProperty {

    public int getValue();

    public int getMax();

    public int getMin();

    public int getDefault();

}
