package io.github.crucible.grimoire.api.events;

public abstract class AbstractListener implements IListener {
    @Override
    public void handle(GrimoireEvent event) {

    }

    @Override
    public int priority() {
        return 0;
    }

    @Override
    public boolean handleCancelled() {
        return false;
    }
}
