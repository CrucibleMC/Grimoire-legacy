package io.github.crucible.omniconfig.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.github.crucible.omniconfig.api.core.SidedConfigType;
import io.github.crucible.omniconfig.api.core.VersioningPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AnnotationConfig {

    String name() default "";

    String version() default "1.0";

    boolean terminateNonInvokedKeys() default true;

    boolean reloadable() default true;

    VersioningPolicy versioningPolicy() default VersioningPolicy.DISMISSIVE;

    SidedConfigType sidedType() default SidedConfigType.COMMON;

}
