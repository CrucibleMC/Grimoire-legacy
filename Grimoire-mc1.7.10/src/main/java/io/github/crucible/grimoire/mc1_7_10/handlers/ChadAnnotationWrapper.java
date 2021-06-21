package io.github.crucible.grimoire.mc1_7_10.handlers;

import io.github.crucible.grimoire.common.events.SubscribeAnnotationWrapper;

public class ChadAnnotationWrapper extends SubscribeAnnotationWrapper {
    private final cpw.mods.fml.common.eventhandler.SubscribeEvent annotation;

    public ChadAnnotationWrapper(cpw.mods.fml.common.eventhandler.SubscribeEvent annotation) {
        this.annotation = annotation;
    }

    @Override
    public boolean annotationPresent() {
        return this.annotation != null;
    }

    @Override
    public boolean receiveCanceled() {
        return this.annotation.receiveCanceled();
    }

    @Override
    public int getEventPriorityOrdinal() {
        return this.annotation.priority().ordinal();
    }
}
