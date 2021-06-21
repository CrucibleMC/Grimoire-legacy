package io.github.crucible.grimoire.mc1_12.handlers;

import io.github.crucible.grimoire.common.events.SubscribeAnnotationWrapper;

public class IncelAnnotationWrapper extends SubscribeAnnotationWrapper {
    private final net.minecraftforge.fml.common.eventhandler.SubscribeEvent annotation;

    public IncelAnnotationWrapper(net.minecraftforge.fml.common.eventhandler.SubscribeEvent annotation) {
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
