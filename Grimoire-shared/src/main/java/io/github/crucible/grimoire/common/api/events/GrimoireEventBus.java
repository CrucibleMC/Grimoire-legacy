/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package io.github.crucible.grimoire.common.api.events;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;
import io.github.crucible.grimoire.common.core.GrimoireCore;
import io.github.crucible.grimoire.common.events.SubscribeAnnotationWrapper;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class GrimoireEventBus implements IEventExceptionHandler {
    private static int maxID = 0;
    private final int busID = maxID++;
    private final ConcurrentHashMap<Object, ArrayList<IEventListener>> listeners = new ConcurrentHashMap<Object, ArrayList<IEventListener>>();
    private IEventExceptionHandler exceptionHandler;
    private boolean shutdown;

    public GrimoireEventBus() {
        ListenerList.resize(busID + 1);
        exceptionHandler = this;
    }

    public GrimoireEventBus(@NotNull IEventExceptionHandler handler) {
        this();
        Preconditions.checkNotNull(handler, "EventBus exception handler can not be null");
        exceptionHandler = handler;
    }

    public void register(Object target) {
        if (listeners.containsKey(target)) {
            return;
        }

        boolean isStatic = target.getClass() == Class.class;
        @SuppressWarnings("unchecked")
        Set<? extends Class<?>> supers = isStatic ? Sets.newHashSet((Class<?>) target) : TypeToken.of(target.getClass()).getTypes().rawTypes();
        for (Method method : (isStatic ? (Class<?>) target : target.getClass()).getMethods()) {
            if (isStatic && !Modifier.isStatic(method.getModifiers()))
                continue;
            else if (!isStatic && Modifier.isStatic(method.getModifiers()))
                continue;

            for (Class<?> cls : supers) {
                try {
                    Method real = cls.getDeclaredMethod(method.getName(), method.getParameterTypes());
                    if (hasAnnotation(real)) {
                        Class<?>[] parameterTypes = method.getParameterTypes();
                        if (parameterTypes.length != 1) {
                            throw new IllegalArgumentException(
                                    "Method " + method + " has @SubscribeEvent annotation, but requires " + parameterTypes.length +
                                            " arguments.  Event handler methods must require a single argument."
                            );
                        }

                        Class<?> eventType = parameterTypes[0];

                        if (!GrimoireEvent.class.isAssignableFrom(eventType)) {
                            throw new IllegalArgumentException("Method " + method + " has @SubscribeEvent annotation, but takes a argument that is not an Event " + eventType);
                        }

                        register(eventType, target, real);
                        break;
                    }
                } catch (NoSuchMethodException e) {
                    // Eat the error, this is not unexpected
                }
            }
        }
    }

    private void register(Class<?> eventType, Object target, Method method) {
        try {
            Constructor<?> ctr = eventType.getConstructor();
            ctr.setAccessible(true);
            GrimoireEvent event = (GrimoireEvent) ctr.newInstance();
            final ASMEventHandler asm = new ASMEventHandler(target, method, IGenericEvent.class.isAssignableFrom(eventType));

            event.getListenerList().register(busID, asm.getPriority(), asm);

            ArrayList<IEventListener> others = listeners.computeIfAbsent(target, k -> new ArrayList<>());
            others.add(asm);
        } catch (Exception e) {
            GrimoireCore.logger.error("[Grimoire] Error registering event handler: {} {}", eventType, method, e);
        }
    }

    public void unregister(Object object) {
        ArrayList<IEventListener> list = listeners.remove(object);
        if (list == null)
            return;
        for (IEventListener listener : list) {
            ListenerList.unregisterAll(busID, listener);
        }
    }

    public boolean post(GrimoireEvent event) {
        if (shutdown) return false;

        IEventListener[] listeners = event.getListenerList().getListeners(busID);
        int index = 0;
        try {
            for (; index < listeners.length; index++) {
                listeners[index].invoke(event);
            }
        } catch (Throwable throwable) {
            exceptionHandler.handleException(this, event, listeners, index, throwable);
            Throwables.propagate(throwable);
        }
        return event.isCancelable() && event.isCanceled();
    }

    public void shutdown() {
        GrimoireCore.logger.warn("[Grimoire] EventBus {} shutting down - future events will not be posted.", busID);
        shutdown = true;
    }

    @Override
    public void handleException(GrimoireEventBus bus, GrimoireEvent event, IEventListener[] listeners, int index, Throwable throwable) {
        GrimoireCore.logger.error("[Grimoire] Exception caught during firing event {}:", event, throwable);
        GrimoireCore.logger.error("Index: {} Listeners:", index);
        for (int x = 0; x < listeners.length; x++) {
            GrimoireCore.logger.error("{}: {}", x, listeners[x]);
        }
    }

    static boolean hasAnnotation(Method method) {
        if (method.isAnnotationPresent(SubscribeEvent.class))
            return true;
        SubscribeAnnotationWrapper wrapper = SubscribeAnnotationWrapper.getWrapper(method);
        return wrapper.annotationPresent();
    }

    static EventPriority getPriority(Method method) {
        if (method.isAnnotationPresent(SubscribeEvent.class)) {
            return method.getAnnotation(SubscribeEvent.class).priority();
        } else {
            SubscribeAnnotationWrapper wrapper = SubscribeAnnotationWrapper.getWrapper(method);
            return EventPriority.values()[wrapper.getEventPriorityOrdinal()];
        }
    }

    static boolean receiveCanceled(Method method) {
        if (method.isAnnotationPresent(SubscribeEvent.class)) {
            return method.getAnnotation(SubscribeEvent.class).receiveCanceled();
        } else {
            SubscribeAnnotationWrapper wrapper = SubscribeAnnotationWrapper.getWrapper(method);
            return wrapper.receiveCanceled();
        }
    }
}
