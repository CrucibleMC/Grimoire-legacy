package io.github.crucible.grimoire.common.api.mixin;

import io.github.crucible.grimoire.common.api.grimmix.IGrimmix;

import java.util.Optional;

public interface IMixinConfiguration {

    public Optional<IGrimmix> getOwner();

    public String getClasspath();

    public ConfigurationType getConfigurationType();

    public boolean isLoaded();

    public boolean isRuntimeGenerated();

    public boolean canLoad();

    public boolean isValid();

    // public void load();


}
