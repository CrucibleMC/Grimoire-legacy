package io.github.crucible.grimoire.common.api.eventbus;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;

import io.github.crucible.grimoire.common.GrimoireCore;
import io.github.crucible.grimoire.common.api.eventbus.CoreEvent.Priority;
import io.github.crucible.grimoire.common.events.SubscribeAnnotationWrapper;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CoreEventBus<T extends CoreEvent> implements IExceptionHandler<T> {
    protected final Class<T> eventClass;
    protected final List<CoreEventHandler> handlerList = new ArrayList<>();
    protected final IExceptionHandler<T> exceptionHandler;
    protected boolean shutdown = false;

    protected CoreEventBus(Class<T> eventClass, IExceptionHandler<T> exceptionHandler) {
        this.eventClass = eventClass;
        this.exceptionHandler = exceptionHandler != null ? exceptionHandler : this;
    }

    public void register(@NotNull Object target) {
        Preconditions.checkNotNull(target);

        for (CoreEventHandler handler : this.handlerList) {
            if (handler.handler == target)
                return;
        }

        try {
            CoreEventHandler handler = new CoreEventHandler(target);

            if (handler.isValid) {
                this.handlerList.add(handler);
            } else {
                GrimoireCore.logger.error("Registered event handler does not have any valid event receivers: {}", target.getClass());
            }
        } catch (Exception ex) {
            GrimoireCore.logger.fatal("Error when trying to register event handler: {}", target.getClass());
            Throwables.propagate(ex);
        }
    }

    public void unregister(Object target) {
        this.handlerList.removeIf(handler -> handler.handler == target);
    }

    public List<CoreEventHandler> getHandlerList() {
        return Collections.unmodifiableList(this.handlerList);
    }

    public boolean post(T event) {
        if (this.shutdown)
            return false;

        try {
            Multimap<Priority, EventReceiver> receiverMap = HashMultimap.create();

            for (CoreEventHandler handler : this.handlerList) {
                for (EventReceiver receiver: handler.receiverList) {
                    if (receiver.eventClass.isAssignableFrom(event.getClass())) {
                        receiverMap.put(receiver.priority, receiver);
                    }
                }
            }

            for (Priority priority : Priority.values()) {
                for (EventReceiver receiver : receiverMap.get(priority)) {
                    if (!event.isCanceled() || receiver.receiveCanceled) {
                        receiver.invoke(event);
                    }
                }
            }
        } catch (Throwable throwable) {
            this.exceptionHandler.handleException(this, event, throwable);
        }
        return event.isCancelable() && event.isCanceled();
    }

    public void shutdown() {
        GrimoireCore.logger.warn("EventBus of type {} shutting down - future events will not be posted.", this.eventClass);
        this.shutdown = true;
    }

    @Override
    public void handleException(CoreEventBus<T> bus, T event, Throwable exception) {
        GrimoireCore.logger.fatal("Exception caught during firing event {}:", event, exception);
        GrimoireCore.logger.fatal("Listeners:");

        for (CoreEventHandler handler : this.handlerList) {
            GrimoireCore.logger.fatal(handler.handler.getClass());
        }

        Throwables.propagate(exception);
    }

    protected boolean hasAnnotation(Method method) {
        if (method.isAnnotationPresent(SubscribeCoreEvent.class))
            return true;
        SubscribeAnnotationWrapper wrapper = SubscribeAnnotationWrapper.getWrapper(method);
        return wrapper.annotationPresent();
    }

    protected Priority getPriority(Method method) {
        if (method.isAnnotationPresent(SubscribeCoreEvent.class))
            return method.getAnnotation(SubscribeCoreEvent.class).priority();
        else {
            SubscribeAnnotationWrapper wrapper = SubscribeAnnotationWrapper.getWrapper(method);
            return Priority.values()[wrapper.getEventPriorityOrdinal()];
        }
    }

    protected boolean receiveCanceled(Method method) {
        if (method.isAnnotationPresent(SubscribeCoreEvent.class))
            return method.getAnnotation(SubscribeCoreEvent.class).receiveCanceled();
        else {
            SubscribeAnnotationWrapper wrapper = SubscribeAnnotationWrapper.getWrapper(method);
            return wrapper.receiveCanceled();
        }
    }

    public static <E extends CoreEvent> CoreEventBus<E> create(@NotNull Class<E> eventClass, IExceptionHandler<E> exceptionHandler) {
        return new CoreEventBus<>(eventClass, exceptionHandler);
    }

    public static <E extends CoreEvent> CoreEventBus<E> create(@NotNull Class<E> eventClass) {
        return create(eventClass, null);
    }

    public static CoreEventBus<CoreEvent> create() {
        return create(CoreEvent.class);
    }

    @SuppressWarnings("unchecked")
    protected class CoreEventHandler {
        protected final Object handler;
        protected final boolean isValid;
        protected final List<EventReceiver> receiverList = new ArrayList<>();

        public CoreEventHandler(Object target) {
            boolean valid = false;
            boolean isStatic = target.getClass() == Class.class;

            Set<? extends Class<?>> supers = isStatic ? Sets.newHashSet((Class<?>) target) : TypeToken.of(target.getClass()).getTypes().rawTypes();

            for (Method method : (isStatic ? (Class<?>) target : target.getClass()).getMethods()) {
                if (isStatic && !Modifier.isStatic(method.getModifiers())) {
                    continue;
                } else if (!isStatic && Modifier.isStatic(method.getModifiers())) {
                    continue;
                }

                for (Class<?> cls : supers) {
                    try {
                        Method real = cls.getDeclaredMethod(method.getName(), method.getParameterTypes());

                        if (CoreEventBus.this.hasAnnotation(real)) {
                            Class<?>[] parameterTypes = method.getParameterTypes();
                            if (parameterTypes.length != 1)
                                throw new IllegalArgumentException(
                                        "Method " + method + " has @SubscribeEvent annotation, but requires " + parameterTypes.length +
                                        " arguments. Event handler methods must require a single argument.");

                            Class<?> eventType = parameterTypes[0];

                            if (CoreEventBus.this.eventClass.isAssignableFrom(eventType)) {
                                valid = true;
                                real.setAccessible(true);
                                this.receiverList.add(new EventReceiver((Class<? extends T>) eventType, real, isStatic ? null : target));
                            }

                            break;
                        }
                    } catch (NoSuchMethodException e) {
                        // Eat the error, this is not unexpected
                    }
                }
            }

            this.handler = target;
            this.isValid = valid;
        }

    }

    protected class EventReceiver {
        protected final Object handler;
        protected final Class<? extends T> eventClass;
        protected final Method receiverMethod;
        protected final Priority priority;
        protected final boolean receiveCanceled;

        public EventReceiver(Class<? extends T> eventClass, Method receiverMethod, Object handler) {
            this.handler = handler;
            this.eventClass = eventClass;
            this.receiverMethod = receiverMethod;
            this.priority = CoreEventBus.this.getPriority(receiverMethod);
            this.receiveCanceled = CoreEventBus.this.receiveCanceled(receiverMethod);
        }

        public void invoke(T event) {
            try {
                this.receiverMethod.invoke(this.handler, event);
            } catch (Exception ex) {
                GrimoireCore.logger.fatal("Error when trying to invoke event receiver!");
                Throwables.propagate(ex);
            }
        }
    }

}
