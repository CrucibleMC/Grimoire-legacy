package io.github.crucible.grimoire.common.api.grimmix;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(TYPE)
public @interface Grimmix {

    public String id();

    public String name() default "";

    public String version() default "1.0.0";

    public long priority() default 0L;

}
