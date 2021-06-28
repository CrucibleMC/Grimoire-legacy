package io.github.crucible.omniconfig.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.github.crucible.omniconfig.api.builders.IOmniconfigBuilder;

/**
 * By design, if annotation config class contains static methods decorated
 * with this annotation, that method will be invoked with an instance of
 * {@link IOmniconfigBuilder} passed as its argument once all property fields
 * are assigned their values loaded from actual file.<br/><br/>
 * Not implemented yet.
 *
 * @author Aizistral
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ConfigLoadCallback {
    // NO-OP
}
