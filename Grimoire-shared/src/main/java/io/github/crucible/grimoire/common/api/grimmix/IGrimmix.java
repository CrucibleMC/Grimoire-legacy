package io.github.crucible.grimoire.common.api.grimmix;

import io.github.crucible.grimoire.common.api.grimmix.lifecycle.LoadingStage;
import io.github.crucible.grimoire.common.api.mixin.IMixinConfiguration;

import java.io.File;
import java.util.List;

public interface IGrimmix {

    public String getID();

    public String getName();

    public long getPriority();

    public String getVersion();

    public LoadingStage getLoadingStage();

    public boolean isValid();

    public boolean isGrimoireGrimmix();

    public List<IMixinConfiguration> getOwnedConfigurations();

    public File getGrimmixFile();

}
