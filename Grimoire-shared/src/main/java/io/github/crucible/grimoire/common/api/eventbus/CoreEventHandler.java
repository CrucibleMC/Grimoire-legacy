package io.github.crucible.grimoire.common.api.eventbus;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.github.crucible.grimoire.common.api.GrimoireConstants;

/**
 * If class is annotated with this, Grimoire will discover it during annotation
 * analysis phase, try to find event buses that match bus names provided through
 * {@link #value()}, and if they indeed exist - automatically subscribe that class
 * to them.<br>
 * Be aware that your class will be treated as static handler - that is, only static
 * methods will be analyzed as potential event receivers.
 *
 * @author Aizistral
 */

@Retention(RUNTIME)
@Target(TYPE)
public @interface CoreEventHandler {

    /**
     * One or more names of event buses that you'd wish your
     * annotated event handler was subscribed to.<br>
     * For Grimoire bus, you can refer to {@link GrimoireConstants#MAIN_BUS_NAME}.
     * @return
     */

    String[] value();

    /**
     * If true, an exception will be raised if one or more of
     * event bus names provided in {@link #value()} cannot be
     * matched with actually existing buses.<br>
     * Default behavior is to simply log a message about it and
     * proceed with our lifes.
     */
    boolean mandatory() default false;

}
