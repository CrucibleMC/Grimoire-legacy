package io.github.crucible.omniconfig.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.github.crucible.omniconfig.api.builders.IOmniconfigBuilder;

/**
 * If annotation config class contains static methods decorated with this
 * annotation, such methods will be invoked with an instance of {@link IOmniconfigBuilder}
 * passed as argument at specified loading stage.
 *
 * @author Aizistral
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ConfigLoadCallback {

    /**
     * Determines at which loading stage this callback should be invoked:<br>
     *
     * <li>{@link Stage#BEFORE_INIT} - when {@link IOmniconfigBuilder} was instantiated,
     * but nothing is done with it yet;</li>
     * <li>{@link Stage#AFTER_INIT} - when {@link IOmniconfigBuilder} was instantiated
     * and property loading phase initiated, but no actual properties are loaded yet;</li>
     * <li>{@link Stage#BEFORE_FINALIZATION} - after property loading has finished, but
     * before builder has transitioned into finalization phase.</li><br>
     *
     * @return Stage at which this callback should be invoked.
     */
    public Stage value() default Stage.BEFORE_FINALIZATION;

    public static enum Stage {
        BEFORE_INIT, AFTER_INIT, BEFORE_FINALIZATION;
    }
}
