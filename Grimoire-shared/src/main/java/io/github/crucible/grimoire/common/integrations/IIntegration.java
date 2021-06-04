package io.github.crucible.grimoire.common.integrations;

public interface IIntegration<T> {
    void initIntegration();
    T getIntegration();
}
