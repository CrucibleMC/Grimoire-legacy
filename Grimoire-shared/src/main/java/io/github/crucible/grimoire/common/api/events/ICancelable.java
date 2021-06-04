package io.github.crucible.grimoire.common.api.events;

public interface ICancelable {
    void cancel();

    boolean isCanceled();

    void setCanceled(boolean state);
}
