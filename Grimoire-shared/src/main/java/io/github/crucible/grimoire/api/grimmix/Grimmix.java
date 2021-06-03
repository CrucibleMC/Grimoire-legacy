package io.github.crucible.grimoire.api.grimmix;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(TYPE)
public @interface Grimmix {

    String modid();

    String name() default "";

    String version() default "1.0.0";

    long priority() default 0L;

}
