package io.github.crucible.omniconfig.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.github.crucible.omniconfig.api.OmniconfigAPI;
import io.github.crucible.omniconfig.api.builders.IOmniconfigBuilder;
import io.github.crucible.omniconfig.api.core.SidedConfigType;
import io.github.crucible.omniconfig.api.core.VersioningPolicy;
import io.github.crucible.omniconfig.api.lib.Version;

/**
 * Must decorate any class that is registered as annotation config via
 * {@link OmniconfigAPI#registerAnnotationConfig(Class)}.
 *
 * @author Aizistral
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AnnotationConfig {

    /**
     * @return Name of config file to be generated.
     * Same as with omniconfig builder, can include subdirectories;
     * for instance: <code>specialmod/Configuration.cfg</code><br/>
     * If name is not specified, simple class name of annotation config
     * class itself will be used for file naming.
     */
    public String name() default "";

    /**
     * @return String representation of current config version.
     * Appropriate {@link Version} object will be constructed from
     * this representation.
     */
    public String version() default "1.0.0";

    /**
     * @return Whether or not non-invoked keys in physical config file
     * should be removed when saving it back to disc.
     * @see IOmniconfigBuilder#terminateNonInvokedKeys(boolean)
     */
    public boolean terminateNonInvokedKeys() default true;

    /**
     * @return Whether physical config file should be watched for changes and
     * automatically reloaded if such occur.
     * @see IOmniconfigBuilder#setReloadable()
     */
    public boolean reloadable() default true;

    /**
     * @return Versioning policy to use for this config file.
     * @see {@link VersioningPolicy}, {@link IOmniconfigBuilder#versioningPolicy(VersioningPolicy)}
     */
    public VersioningPolicy policy() default VersioningPolicy.DISMISSIVE;

    /**
     * @return Sided type this config file should have.
     * @see SidedConfigType
     */
    public SidedConfigType sided() default SidedConfigType.COMMON;

}
