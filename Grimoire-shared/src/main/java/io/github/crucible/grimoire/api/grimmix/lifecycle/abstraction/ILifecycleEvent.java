package io.github.crucible.grimoire.api.grimmix.lifecycle.abstraction;

import io.github.crucible.grimoire.api.grimmix.IGrimmix;
import io.github.crucible.grimoire.api.grimmix.lifecycle.LoadingStage;

public interface ILifecycleEvent {

    LoadingStage getLoadingStage();

    IGrimmix getEventOwner();

}
