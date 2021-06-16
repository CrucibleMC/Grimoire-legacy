package io.github.crucible.omniconfig.api.annotation;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import io.github.crucible.omniconfig.api.core.IOmniconfig;

public interface IAnnotationConfigRegistry {

    public Collection<Class<?>> getRegisteredAnnotationConfigs();

    public Optional<IOmniconfig> getAssociatedOmniconfig(Class<?> annotationConfigClass);

}
