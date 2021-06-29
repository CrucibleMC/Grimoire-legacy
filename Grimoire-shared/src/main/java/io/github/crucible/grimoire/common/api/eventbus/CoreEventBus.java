package io.github.crucible.grimoire.common.api.eventbus;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;

import io.github.crucible.grimoire.common.GrimoireCore;
import io.github.crucible.grimoire.common.api.eventbus.CoreEvent.Priority;
import io.github.crucible.grimoire.common.events.SubscribeAnnotationWrapper;

/**
 * Glorified version-independent event bus implementation, stripped of
 * all ASM thingies Forge's event buses really need for some reason.<br/>
 * Designed with maximum extensibility in mind.
 *
 * @param <T> Type of event all events dispatched via this bus must inherit.
 * @author Aizistral
 */

public class CoreEventBus<T extends CoreEvent> implements IExceptionHandler<T> {
    private static final List<CoreEventBus<? extends CoreEvent>> busRegistry = new ArrayList<>();
    protected static int nextBusID = 0;

    /**
     * Numeric ID of this bus in bus registry
     */
    protected final int id;

    /**
     * {@link String}-form ID of this bus.
     */
    protected final String name;

    /**
     * Event {@link Class} that all events dispatched via this
     * bus must inherit.
     */
    protected final Class<T> eventClass;

    /**
     * List of wrapped handlers subscribed to this bus instance.
     */
    protected final List<CoreEventHandler> handlerList = new ArrayList<>();

    /**
     * Exception handler for this bus, normally the bus itself.
     */
    protected final IExceptionHandler<T> exceptionHandler;

    /**
     * Whether or not this bus was forced to shutdown.
     */
    protected boolean shutdown = false;

    protected CoreEventBus(Class<T> eventClass, String busName, IExceptionHandler<T> exceptionHandler) {
        this.eventClass = eventClass;
        this.name = busName;
        this.exceptionHandler = exceptionHandler != null ? exceptionHandler : this;
        this.id = nextBusID;

        busRegistry.add(this);
        nextBusID++;
    }

    /**
     * @return Numeric ID of this bus in bus registry.
     */
    public int getID() {
        return this.id;
    }

    /**
     * @return {@link String}-form ID of this bus.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Register given object as an event handler for this bus.<br/>
     * It is anticipated that given object will have one or more public methods
     * decorated either with {@link SubscribeCoreEvent} annotation, or with
     * version-dependent <code>@SubscribeEvent</code> annotation provided by
     * Forge itself; and receive a single argument, that being event type it
     * wants to handle, which extends base event class this bus is designed
     * to dispatch.<br/><br/>
     *
     * It is possible to register two types of handler - instance handler
     * and static handler. First must be an instance of class that contains
     * non-static event receiver methods; second should be {@link Class} of
     * handler which contains static event receiver methods.<br/><br/>
     *
     * Registered handlers are wrapped into {@link CoreEventHandler} object
     * for interaction convenience.
     *
     * @param target Something to register as event handler.
     */
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
                GrimoireCore.logger.error("Registered event handler does not have any valid event receivers: {}", target.getClass() == Class.class ? target : target.getClass());
            }
        } catch (Exception ex) {
            GrimoireCore.logger.fatal("Error when trying to register event handler: {}", target.getClass());
            Throwables.propagate(ex);
        }
    }

    /**
     * If given instance or static handler exists in the list of current
     * handlers, remove it from there.
     *
     * @param target Handler in question.
     */
    public void unregister(Object target) {
        this.handlerList.removeIf(handler -> handler.handler == target);
    }

    /**
     * @return List of all wrapped event handlers currently subscribed
     * to this event bus.
     */
    public List<CoreEventHandler> getHandlerList() {
        return Collections.unmodifiableList(this.handlerList);
    }

    /**
     * Post an event that all subscribed event handlers will receive.
     *
     * @param event Event to post.
     * @return True if event is cancelable and was canceled; false at all
     * other times.
     */
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

    /**
     * Stop this event bus from posting any subsequent events, and
     * drop the list of registered event handlers.
     */
    public void shutdown() {
        GrimoireCore.logger.warn("EventBus of type {} shutting down - future events will not be posted.", this.eventClass);
        this.shutdown = true;
        this.handlerList.clear();
    }

    /**
     * Default exception handling logic for when an exception arises
     * during event dispatching.
     */
    @Override
    public void handleException(CoreEventBus<T> bus, T event, Throwable exception) {
        GrimoireCore.logger.fatal("Exception caught during firing event {}:", event, exception);
        GrimoireCore.logger.fatal("Listeners:");

        for (CoreEventHandler handler : this.handlerList) {
            GrimoireCore.logger.fatal(handler.handler.getClass());
        }

        Throwables.propagate(exception);
    }

    /**
     * @param method Method to check.
     * @return True if it has some form of <code>@SubscribeEvent</code> annotation,
     * false if not.
     */
    protected boolean hasAnnotation(Method method) {
        if (method.isAnnotationPresent(SubscribeCoreEvent.class))
            return true;
        SubscribeAnnotationWrapper wrapper = SubscribeAnnotationWrapper.getWrapper(method);
        return wrapper.annotationPresent();
    }

    /**
     * @param method Method to check.
     * @return If this method has some form of <code>@SubscribeEvent</code> annotation,
     * {@link Priority} provided by that annotation is returned.
     * @throws NullPointerException if event does not have any form of
     * <code>@SubscribeEvent</code> annotation.
     */
    protected Priority getPriority(Method method) throws NullPointerException {
        if (method.isAnnotationPresent(SubscribeCoreEvent.class))
            return method.getAnnotation(SubscribeCoreEvent.class).priority();
        else {
            SubscribeAnnotationWrapper wrapper = SubscribeAnnotationWrapper.getWrapper(method);
            return Priority.values()[wrapper.getEventPriorityOrdinal()];
        }
    }

    /**
     * @param method Method to check.
     * @return If this method has some form of <code>@SubscribeEvent</code> annotation,
     * <code>receiveCanceled</code> parameter provided by annotation is returned.
     * @throws NullPointerException if event does not have any form of
     * <code>@SubscribeEvent</code> annotation.
     */
    protected boolean receiveCanceled(Method method) throws NullPointerException {
        if (method.isAnnotationPresent(SubscribeCoreEvent.class))
            return method.getAnnotation(SubscribeCoreEvent.class).receiveCanceled();
        else {
            SubscribeAnnotationWrapper wrapper = SubscribeAnnotationWrapper.getWrapper(method);
            return wrapper.receiveCanceled();
        }
    }

    /**
     * Create new event bus.
     *
     * @param eventClass Class of events that should be dispatched by this bus.
     * @param busName {@link String}-form name of this bus.
     * @param exceptionHandler Exception handler for bus, or no to let bus handle
     * exceptions with default logic.
     * @return New {@link CoreEventBus} instance.
     */
    public static <E extends CoreEvent> CoreEventBus<E> create(@NotNull Class<E> eventClass, @NotNull String busName, IExceptionHandler<E> exceptionHandler) {
        return new CoreEventBus<>(eventClass, busName, exceptionHandler);
    }

    /**
     * Create new event bus.
     *
     * @param eventClass Class of events that should be dispatched by this bus.
     * @param busName {@link String}-form name of this bus.
     * @return New {@link CoreEventBus} instance, with its exception handler being
     * bus itself.
     */
    public static <E extends CoreEvent> CoreEventBus<E> create(@NotNull Class<E> eventClass, String busName) {
        return create(eventClass, busName, null);
    }

    /**
     * Create new event bus.
     *
     * @param busName {@link String}-form name of this bus.
     * @return New {@link CoreEventBus} instance, with its exception handler being
     * bus itself, and event type it dispatches being anything that extends {@link CoreEvent}.
     */
    public static CoreEventBus<CoreEvent> create(String busName) {
        return create(CoreEvent.class, busName);
    }

    /**
     * @return Unmodifiable list of all {@link CoreEventBus}es ever created.
     */
    public static List<CoreEventBus<? extends CoreEvent>> getBusRegistry() {
        return Collections.unmodifiableList(busRegistry);
    }

    /**
     * Try to find bus in bus registry by its {@link String}-form name.
     *
     * @param busName Name to seek.
     * @return Bus instance if bus with such name exists, or an empty
     * {@link Optional} if no such bus can be located.
     */
    public static Optional<CoreEventBus<? extends CoreEvent>> findBus(String busName) {
        for (CoreEventBus<? extends CoreEvent> bus : busRegistry) {
            if (Objects.equal(bus.getName(), busName))
                return Optional.of(bus);
        }

        return Optional.empty();
    }

    /**
     * Wraps registered event handler, for convenience of internal interaction.
     *
     * @author Aizistral
     */

    @SuppressWarnings("unchecked")
    protected class CoreEventHandler {
        protected final Object handler;
        protected final boolean isValid;
        protected final List<EventReceiver> receiverList = new ArrayList<>();

        public CoreEventHandler(Object target) {
            boolean valid = false;
            boolean isStatic = target.getClass() == Class.class;

            if (isStatic) {
                GrimoireCore.logger.info("Analyzing static handler " + target);
            }

            Set<? extends Class<?>> supers = isStatic ? Sets.newHashSet((Class<?>) target) : TypeToken.of(target.getClass()).getTypes().rawTypes();

            // TODO Maybe use getDeclaredMethods() below
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

    /**
     * Wraps individual event receiver method from its associated handler,
     * as well as some data about that method.
     *
     * @author Aizistral
     */

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
