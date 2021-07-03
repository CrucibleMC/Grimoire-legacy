package io.github.crucible.omniconfig.api.annotation.properties;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.github.crucible.omniconfig.api.OmniconfigConstants;
import io.github.crucible.omniconfig.backing.Configuration;

/**
 * Decorate static <code>double</code> field with this annotation
 * to mark it as annotation config property. Default value of property
 * will be equal to value annotated field initially has.
 *
 * @author Aizistral
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigDouble {

    /**
     * @return Name of this property in config file.
     * If not specified, field name will be used instead.
     */
    String name() default "";

    /**
     * @return Config category this property should belong to.
     * It is allowed to specify subcategory hierarchy by separating
     * category names with {@link OmniconfigConstants#CATEGORY_SPLITTER}.
     */
    String category() default OmniconfigConstants.GENERAL_CATEGORY;

    /**
     * @return Human-readable comment describing the purpose
     * of this config property.
     */
    String comment() default OmniconfigConstants.DEFAULT_COMMENT;

    /**
     * @return Minimum value this property is allowed to take.
     */
    double min() default 0;

    /**
     * @return Maximum value this property is allowed to take.
     */
    double max() default OmniconfigConstants.STANDART_INTEGER_LIMIT;

}
