package io.github.crucible.grimoire.common.api.eventbus;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.github.crucible.grimoire.common.api.eventbus.CoreEvent.Priority;

/**
 * Serves as version-independent edition of Forge's
 * <code>@SubscribeEvent</code> annotation, providing exactly
 * the same data about event receiver it decorates.<br><br>
 *
 * It also allows to avoid conflict with Forge event buses when
 * subscribing single event handler instance to both {@link CoreEventBus}
 * and Forge bus. While {@link CoreEventBus} will handle this just
 * fine regardless of whether methods are decorated with
 * <code>@SubscribeEvent</code> or {@link SubscribeCoreEvent} annotation,
 * Forge bus will fail, as it demands that all methods decorated with
 * <code>@SubscribeEvent</code> annotation specifically receive events
 * that extend Forge's implementation of <code>Event</code>,
 * which {@link CoreEvent} itself does not inherit.
 *
 * @author Aizistral
 */

@Retention(value = RUNTIME)
@Target(value = METHOD)
public @interface SubscribeCoreEvent {

    /**
     * @return {@link Priority} this event receiver should have,
     * compared to other receivers.
     */
    public Priority priority() default Priority.NORMAL;

    /**
     * @return True if this event receiver should receive
     * dispatched event even if it was canceled by some other
     * receiver earlier.
     */
    public boolean receiveCanceled() default false;

}
