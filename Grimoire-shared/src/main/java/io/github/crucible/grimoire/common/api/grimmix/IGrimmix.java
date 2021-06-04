package io.github.crucible.grimoire.common.api.grimmix;

import io.github.crucible.grimoire.common.api.configurations.IMixinConfiguration;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.LoadingStage;
import io.github.crucible.grimoire.common.integrations.IntegrationManager;

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
