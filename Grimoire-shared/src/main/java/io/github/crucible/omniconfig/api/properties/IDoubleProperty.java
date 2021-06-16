package io.github.crucible.omniconfig.api.properties;

public interface IDoubleProperty extends IAbstractProperty {

    public double getValue();

    public double getMax();

    public double getMin();

    public double getDefault();

}
