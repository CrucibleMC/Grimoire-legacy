package io.github.crucible.grimoire.common.api.grimmix.lifecycle.abstraction;

import io.github.crucible.grimoire.common.api.grimmix.IGrimmix;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.LoadingStage;

public interface ILifecycleEvent {

    LoadingStage getLoadingStage();

    IGrimmix getEventOwner();

}
