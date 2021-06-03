package io.github.crucible.grimoire.api.events;

public interface IListener {
    void handle(GrimoireEvent event);

    int priority();

    boolean handleCancelled();
}
