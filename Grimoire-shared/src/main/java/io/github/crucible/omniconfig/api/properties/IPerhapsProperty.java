package io.github.crucible.omniconfig.api.properties;

import io.github.crucible.omniconfig.api.lib.Perhaps;

public interface IPerhapsProperty extends IAbstractProperty {

    public Perhaps getValue();

    public Perhaps getMax();

    public Perhaps getMin();

    public Perhaps getDefault();

}
