package io.github.crucible.omniconfig.annotation.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.github.crucible.omniconfig.core.Configuration.SidedConfigType;
import io.github.crucible.omniconfig.core.Configuration.VersioningPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AnnotationConfig {

    String name() default "";

    String version() default "1.0";

    boolean terminateNonInvokedKeys() default true;

    boolean reloadable() default true;

    boolean caseSensitiveCategories() default true;

    VersioningPolicy versioningPolicy() default VersioningPolicy.DISMISSIVE;

    SidedConfigType sidedType() default SidedConfigType.COMMON;

}
