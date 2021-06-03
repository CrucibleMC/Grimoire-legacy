package io.github.crucible.grimoire.api.events;

public interface ICancelable {
    void cancel();

    boolean isCanceled();

    void setCanceled(boolean state);
}
