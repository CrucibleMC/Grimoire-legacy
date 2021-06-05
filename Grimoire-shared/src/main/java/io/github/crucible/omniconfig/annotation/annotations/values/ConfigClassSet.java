package io.github.crucible.omniconfig.annotation.annotations.values;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.github.crucible.omniconfig.core.Configuration;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigClassSet {

    String name() default "";

    String category() default Configuration.CATEGORY_GENERAL;

    String comment() default "Undocumented property";

}
