package io.github.crucible.grimoire.api.events;

public abstract class CancelableEvent extends GrimoireEvent implements ICancelable {
    protected boolean isCancelled;

    @Override
    public void cancel() {
        isCancelled = true;
    }

    @Override
    public boolean isCanceled() {
        return isCancelled;
    }

    @Override
    public void setCanceled(boolean state) {
        isCancelled = state;
    }
}
