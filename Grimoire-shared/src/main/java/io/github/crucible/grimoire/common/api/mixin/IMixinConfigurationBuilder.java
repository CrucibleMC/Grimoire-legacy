package io.github.crucible.grimoire.common.api.mixin;

import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.MixinEnvironment.Phase;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;

import io.github.crucible.grimoire.common.api.grimmix.lifecycle.IConfigBuildingEvent;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.ICoreLoadEvent;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.IModLoadEvent;

/**
 * Powerful tool that allows you to build your mixin configuration .json
 * at runtime instead of having to ship it as part of your mod. You can start this
 * builder in {@link IConfigBuildingEvent} of your grimmix controller.<br><br>
 *
 * Note that of all builder methods one that is mandatory to call is {@link #mixinPackage(String)},
 * otherwise your configuration will be recognized as invalid when you finish it with {@link #build()}.
 * <br><br>
 *
 * It is also worth noting that configurations created with this builder <b>need not to be registered
 * in either {@link ICoreLoadEvent} or {@link IModLoadEvent}</b>; they will automatically be registered
 * by Grimoire itself with {@link ConfigurationType} assigned via
 * {@link #configurationType(ConfigurationType)} method of this builder.
 *
 * @author Aizistral
 */

public interface IMixinConfigurationBuilder {

    /**
     * Define configuration phase for Mixin itself that this configuration shall have.
     * Don't touch this unless you know what you need it for.<br><br>
     *
     * By default this remains unspecified ({@link Phase#DEFAULT} will be targeted).
     *
     * @param envPhase Phase.
     * @return This builder instance.
     */
    IMixinConfigurationBuilder targetEnvironment(MixinEnvironment.Phase envPhase);

    /**
     * Specify refmap this mixin configuration will be pointing to.<br><br>
     *
     * By default no refmap is specified.
     *
     * @param refmap Path to refmap.
     * @return This builder instance.
     */
    IMixinConfigurationBuilder refmap(String refmap);

    /**
     * Mark mixins of this configuration as mandatory. If Mixin itself
     * fails to apply them, it will raise an application-crashing exception.
     * This is not the case for non-mandatory mixins, in case of which
     * only an error message will be logged.<br><br>
     *
     * By default this is set to false.
     *
     * @param required True for mandatory configuration, false for non-mandatory.
     * @return This builder instance.
     */
    IMixinConfigurationBuilder required(boolean required);

    /**
     * Specify package in which your mixins reside. It is important to note
     * that entire package will be excluded from being visible to classloader
     * when your configuration is loaded.
     *
     * @param mixinPackage Package with your mixins.
     * @return This builder instance.
     */
    IMixinConfigurationBuilder mixinPackage(String mixinPackage);

    /**
     * Specify list of mixin classes that that will be applied as part of
     * this configuration, on both client and dedicated server environment.
     * Class locations must be specified relative to mixin package specifed by
     * {@link #mixinPackage(String)} call.<br><br>
     *
     * One advantage this builder has is that it allows you to use wildcards
     * in class name specification. Whatever file your grimmix resides within
     * will be analyzed for classes that match wildcard specification, and
     * class list in generated .json file will include all that do. A couple
     * examples on how to use wildcards:<br><br>
     *
     * String of format <code>"*"</code> will match all classes contained within
     * mixin package specifed by by {@link #mixinPackage(String)} call, including
     * classes in all subpackages.<br><br>
     *
     * String of format <code>"common.*"</code> will match all classes that reside
     * in <code>common</code> subpackage of mixin package, including classes in
     * all subpackages of <code>common</code> subpackage, if such exist.<br><br>
     *
     * String of format <code>"Mixin*"</code> will match all classes that reside
     * in the root of mixin package and which have their name starting with
     * <code>Mixin</code>.
     *
     * @param mixinClasses List of common mixins for this configuration.
     * @return This builder instance.
     */
    IMixinConfigurationBuilder commonMixins(String... mixinClasses);

    /**
     * Specify list of mixin classes that that will be applied as part of
     * this configuration, in client environment only. Supports the use
     * of wildcards; see {@link #commonMixins(String...)} for more details.
     *
     * @param mixinClasses List of client mixins for this configuration.
     * @return This builder instance.
     * @see #commonMixins(String...)
     */
    IMixinConfigurationBuilder clientMixins(String... mixinClasses);

    /**
     * Specify list of mixin classes that that will be applied as part of
     * this configuration, in dedicated server environment only. Supports the
     * use of wildcards; see {@link #commonMixins(String...)} for more details.
     *
     * @param mixinClasses List of client mixins for this configuration.
     * @return This builder instance.
     * @see #commonMixins(String...)
     */
    IMixinConfigurationBuilder serverMixins(String... mixinClasses);

    /**
     * Specify priority of this mixin set relative to other configurations.<br><br>
     *
     * By default it remains unspecified (priority 1000 will be assigned).
     *
     * @param priority Numeric priority.
     * @return This builder instance.
     */
    IMixinConfigurationBuilder priority(int priority);

    /**
     * Causes the mixin processor to overwrite the source file property in target
     * classes with the source file of the mixin class. This can be useful when
     * debugging mixins.<br><br>
     *
     * By default this is set to false.
     *
     * @param setOrNot True to overwrite, false to not.
     * @return This builder instance.
     */
    IMixinConfigurationBuilder setSourceFile(boolean setOrNot);

    /**
     * Promotes all <code>DEBUG</code>-level log messages to <code>INFO</code> level
     * for this mixin set.<br><br>
     *
     * By default this is set to false.
     *
     * @param verbose True to promote, false to not.
     * @return This builder instance.
     */
    IMixinConfigurationBuilder verbose(boolean verbose);

    /**
     * Specify the name of an optional companion plugin class for the mixin configuration
     * which can tweak the mixin configuration programmatically at runtime.<br><br>
     *
     * By default no plugin is specified.
     *
     * @param pluginClass Fully qualified name of class implementing
     * {@link IMixinConfigPlugin}.
     * @return This builder instance.
     */
    IMixinConfigurationBuilder setConfigurationPlugin(String pluginClass);

    /**
     * Specify {@link ConfigurationType} for this configuration. Since runtime-built
     * configurations are not registered in {@link IModLoadEvent} or {@link ICoreLoadEvent},
     * this is your only opportunity to do so.<br><br>
     *
     * By default {@link ConfigurationType#CORE} is used.
     *
     * @param type
     * @return
     * @see {@link ConfigurationType}
     */
    IMixinConfigurationBuilder configurationType(ConfigurationType type);

    /**
     * Finish building this configuration and get {@link IMixinConfiguration} instance
     * associated with it. Calling this method more than once on same builder instance will
     * result in {@link IllegalStateException} being thrown.
     *
     * @return {@link IMixinConfiguration} instance.
     */
    IMixinConfiguration build();

}
