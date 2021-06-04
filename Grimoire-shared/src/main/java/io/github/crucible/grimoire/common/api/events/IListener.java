package io.github.crucible.grimoire.common.api.events;

public interface IListener {
    void handle(GrimoireEvent event);

    int priority();

    boolean handleCancelled();
}
