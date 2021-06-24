package io.github.crucible.grimoire.common.api.grimmix;

import io.github.crucible.grimoire.common.api.configurations.IMixinConfiguration;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.LoadingStage;

import java.io.File;
import java.util.List;

public interface IGrimmix {

    public String getModID();

    public String getName();

    public long getPriority();

    public String getVersion();

    public LoadingStage getLoadingStage();

    public boolean isValid();

    public boolean isGrimoireGrimmix();

    public List<IMixinConfiguration> getOwnedConfigurations();

    public File getGrimmixFile();

}
