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

    public String name() default "";

    public String version() default "1.0.0";

    public boolean terminateNonInvokedKeys() default true;

    public boolean reloadable() default true;

    public VersioningPolicy policy() default VersioningPolicy.DISMISSIVE;

    public SidedConfigType sided() default SidedConfigType.COMMON;

}
