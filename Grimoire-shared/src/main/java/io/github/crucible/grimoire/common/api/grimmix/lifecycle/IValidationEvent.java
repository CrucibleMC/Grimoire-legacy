package io.github.crucible.grimoire.common.api.grimmix.lifecycle;

public interface IValidationEvent extends ILifecycleEvent {

    public void invalidate();

    public boolean isValid();

}
