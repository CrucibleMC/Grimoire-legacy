package io.github.crucible.grimoire.common.events;

import java.lang.reflect.Method;
import java.util.function.Function;

import com.google.common.base.Preconditions;

import io.github.crucible.grimoire.common.core.GrimoireCore;

public abstract class SubscribeAnnotationWrapper {
    private static Function<Method, SubscribeAnnotationWrapper> wrapperFactory = null;

    protected SubscribeAnnotationWrapper() {
        // NO-OP
    }

    public abstract boolean annotationPresent();

    public abstract boolean receiveCanceled();

    public abstract int getEventPriorityOrdinal();

    public static void setWrapperFactory(Function<Method, SubscribeAnnotationWrapper> factory) {
        Preconditions.checkArgument(wrapperFactory == null, "Factory already set!");
        wrapperFactory = factory;
    }

    public static SubscribeAnnotationWrapper getWrapper(Method method) {
        return wrapperFactory.apply(method);
    }

}
