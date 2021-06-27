package io.github.crucible.omniconfig.api.builders;

import java.util.function.Function;

import io.github.crucible.omniconfig.api.lib.Perhaps;
import io.github.crucible.omniconfig.api.properties.IPerhapsProperty;

public interface IPerhapsPropertyBuilder extends IAbstractPropertyBuilder<IPerhapsProperty, IPerhapsPropertyBuilder> {

    public IPerhapsPropertyBuilder max(double percent);

    public IPerhapsPropertyBuilder min(double percent);

    public IPerhapsPropertyBuilder minMax(double percent);

    public IPerhapsPropertyBuilder validator(Function<Perhaps, Perhaps> validator);

}
