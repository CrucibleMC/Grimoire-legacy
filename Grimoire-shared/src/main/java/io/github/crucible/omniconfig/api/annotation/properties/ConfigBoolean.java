package io.github.crucible.omniconfig.api.annotation.properties;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.github.crucible.omniconfig.backing.Configuration;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigBoolean {

    String name() default "";

    String category() default Configuration.CATEGORY_GENERAL;

    String comment() default "Undocumented property";

}
