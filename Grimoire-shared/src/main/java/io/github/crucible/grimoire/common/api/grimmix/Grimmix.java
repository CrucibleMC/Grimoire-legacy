package io.github.crucible.grimoire.common.api.grimmix;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.github.crucible.grimoire.common.api.grimmix.lifecycle.LoadingStage;

/**
 * Indicates annotated class as Grimmix controller. Grimoire will
 * automatically discover and load all annotated controllers from .jar files
 * within Minecraft mods folder, as well as all that reside on classpath; latter
 * is important for development environments.<br/><br/>
 *
 * All classes decorated with this annotation must inherit {@link GrimmixController},
 * and have at least one public constructor that requires no arguments; otherwise
 * controller will cause and error state at {@link LoadingStage#CONSTRUCTION}.
 *
 * @author Aizistral
 */

@Retention(RUNTIME)
@Target(TYPE)
public @interface Grimmix {

    /**
     * @return {@link String}-form ID used to distinguish this grimmix among others.
     * Must only contain latin letters or digits.
     */
    public String id();

    /**
     * @return Textual name of this grimmix.
     */
    public String name() default "";

    /**
     * @return Version of this grimmix.
     */
    public String version() default "1.0.0";

    /**
     * @return Priority this grimmix should have compared to other grimmixes.
     * Matters for order of dispatching lifecycle events.
     */
    public long priority() default 0L;

}
