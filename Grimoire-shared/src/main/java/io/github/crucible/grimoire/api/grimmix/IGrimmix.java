package io.github.crucible.grimoire.api.grimmix;

import io.github.crucible.grimoire.api.configurations.IMixinConfiguration;
import io.github.crucible.grimoire.api.grimmix.lifecycle.LoadingStage;

import java.io.File;
import java.util.List;

public interface IGrimmix {

    String getModID();

    String getName();

    long getPriority();

    String getVersion();

    LoadingStage getLoadingStage();

    boolean isValid();

    List<IMixinConfiguration> getOwnedConfigurations();

    File getGrimmixFile();

}
