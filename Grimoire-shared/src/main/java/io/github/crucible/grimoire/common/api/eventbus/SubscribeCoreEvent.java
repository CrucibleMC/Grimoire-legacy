package io.github.crucible.grimoire.common.api.eventbus;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.github.crucible.grimoire.common.api.eventbus.CoreEvent.Priority;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(value = RUNTIME)
@Target(value = METHOD)
public @interface SubscribeCoreEvent {

    public Priority priority() default Priority.NORMAL;
    public boolean receiveCanceled() default false;

}
