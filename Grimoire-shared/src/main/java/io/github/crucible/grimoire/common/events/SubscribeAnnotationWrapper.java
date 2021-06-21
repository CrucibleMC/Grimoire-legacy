package io.github.crucible.grimoire.common.events;

import java.lang.reflect.Method;

import io.github.crucible.grimoire.common.core.GrimoireCore;

public abstract class SubscribeAnnotationWrapper {

    protected SubscribeAnnotationWrapper() {
        // NO-OP
    }

    public abstract boolean annotationPresent();

    public abstract boolean receiveCanceled();

    public abstract int getEventPriorityOrdinal();

    public static SubscribeAnnotationWrapper getWrapper(Method method) {
        String mc = GrimoireCore.INSTANCE.getMCVersion();

        if (mc.equals("1.7.10"))
            return new ChadAnnotationWrapper(method.getAnnotation(cpw.mods.fml.common.eventhandler.SubscribeEvent.class));
        else
            return new IncelAnnotationWrapper(method.getAnnotation(net.minecraftforge.fml.common.eventhandler.SubscribeEvent.class));
    }

    private static class ChadAnnotationWrapper extends SubscribeAnnotationWrapper {
        private final cpw.mods.fml.common.eventhandler.SubscribeEvent annotation;

        private ChadAnnotationWrapper(cpw.mods.fml.common.eventhandler.SubscribeEvent annotation) {
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

    private static class IncelAnnotationWrapper extends SubscribeAnnotationWrapper {
        private final net.minecraftforge.fml.common.eventhandler.SubscribeEvent annotation;

        private IncelAnnotationWrapper(net.minecraftforge.fml.common.eventhandler.SubscribeEvent annotation) {
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

}
