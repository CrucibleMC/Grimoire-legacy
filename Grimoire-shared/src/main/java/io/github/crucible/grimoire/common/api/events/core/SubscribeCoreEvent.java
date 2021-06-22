package io.github.crucible.grimoire.common.api.events.core;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(value = RUNTIME)
@Target(value = METHOD)
public @interface SubscribeCoreEvent {

    CoreEvent.Priority priority() default CoreEvent.Priority.NORMAL;
    boolean receiveCanceled() default false;

}
