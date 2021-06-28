package io.github.crucible.omniconfig.api.builders;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import io.github.crucible.omniconfig.api.core.SidedConfigType;
import io.github.crucible.omniconfig.api.OmniconfigAPI;
import io.github.crucible.omniconfig.api.core.IOmniconfig;
import io.github.crucible.omniconfig.api.core.VersioningPolicy;
import io.github.crucible.omniconfig.api.lib.Perhaps;
import io.github.crucible.omniconfig.api.lib.Version;
import io.github.crucible.omniconfig.api.properties.IBooleanProperty;
import io.github.crucible.omniconfig.api.properties.IDoubleProperty;
import io.github.crucible.omniconfig.api.properties.IEnumProperty;
import io.github.crucible.omniconfig.api.properties.IFloatProperty;
import io.github.crucible.omniconfig.api.properties.IIntegerProperty;
import io.github.crucible.omniconfig.api.properties.IPerhapsProperty;
import io.github.crucible.omniconfig.api.properties.IStringListProperty;
import io.github.crucible.omniconfig.api.properties.IStringProperty;

import static io.github.crucible.omniconfig.api.builders.BuildingPhase.*;

/**
 * An interface for interaction with your personalized omniconfig builder.
 * You can get builder instance from {@link OmniconfigAPI#configBuilder(String, Version, SidedConfigType)}.<br/>
 * Be aware that building every omniconfig file is done in three stages:<br/><br/>
 *
 * <b>1)</b> Initialization;<br/>
 * <b>2)</b> Loading properties;<br/>
 * <b>3)</b> Finalization.<br/><br/>
 *
 * These phases are represented by respective {@link BuildingPhase} enum constants.
 * In order for building to be performed correctly, you <b>must</b> go over these phases in specific
 * order described above, only calling any of the builder methods on particular phase they belong to.
 * <br/><br/>
 *
 * For that sake, methods of this interface are decorated with {@link PhaseOnly} annotation, specifying
 * during which phases this method can be called.<br/><br/>
 *
 * If a method is decorated with {@link Finalizes}, it means that calling it explicitly ends building
 * phase specified as annotation value. While <code>INITIALIZATION</code> phase must be ended by
 * calling {@link #loadFile()} method, <code>PROPERTY_LOADING</code> phase doesn't have such method;
 * it can be ended either by calling any of the methods from <code>FINALIZATION</code> phase, all
 * of which are currently optional, or by right away calling {@link #build()} method, which can end
 * both <code>INITIALIZATION</code> and <code>FINALIZATION</code> phases.<br/><br/>
 *
 * After <code>FINALIZATION</code> phase is over, builder is automatically invalidated;
 * calling any of its methods in such case will result in {@link IllegalStateException} being thrown.
 *
 * @author Aizistral
 */

public interface IOmniconfigBuilder {

    /**
     * Define a {@link VersioningPolicy} applied to your config file.
     * By default {@link VersioningPolicy#DISMISSIVE} will be used.
     *
     * @param policy Policy of your choice.
     * @return This builder instance.
     * @see VersioningPolicy
     */

    @PhaseOnly(INITIALIZATION)
    public IOmniconfigBuilder versioningPolicy(VersioningPolicy policy);


    /**
     * Allows you to dynamically choose versioning policy, depending on what version outdated
     * config file has. As an example, consider the following snippet:<br/>
     *
     * <pre>
     * if (foundVersion.isOlderThan("2.0.0")) {
     *     return {@link VersioningPolicy#AGGRESSIVE};
     * } else {
     *     return {@link VersioningPolicy#RESPECTFUL}
     * }</pre>
     *
     * This will result in old config file being entirely discarded if its version is lower
     * than <code>2.0.0</code>, but if it is exactly <code>2.0.0</code> or above, yet still
     * differs from current version - respectful policy will be used.<br/><br/>
     *
     * Result of determinator's execution will override versioning policy specified by
     * {@link #versioningPolicy(VersioningPolicy)}, if such was invoked.
     *
     * @param determinator Your personalized policy backflips.
     * @return This builder instance.
     */
    @PhaseOnly(INITIALIZATION)
    public IOmniconfigBuilder versioningPolicyBackflips(Function<Version, VersioningPolicy> determinator);

    /**
     * Allows you define resolution strategy for properties that are present in physical file,
     * but do not match any of the properties retained during omniconfig property loading phase;
     * such will appear if you remove some properties from config in mod update, and end
     * user will update to new mod version from old one that still had these properties and generated
     * its config file accordingly. Default implementation of Forge configs has a "feature" where
     * properties that are no longer retained at runtime still persist in physical file, along with their
     * category hierachy, but without any comments. The only way to get rid of them is if user deletes
     * their file manually, otherwise they will remain stuck there permanently.<br/><br/>
     *
     * For omniconfig files, however, default behavior is to remove properties that are not retained
     * from physical file once building is finished. You can set this to false in case you desire that
     * they always persist.
     *
     * @param terminate False if unretained properties should persist in physical file, true if not.
     * @return This builder instance.
     */
    @PhaseOnly(INITIALIZATION)
    public IOmniconfigBuilder terminateNonInvokedKeys(boolean terminate);

    /**
     * Load physical configuration file from disk and proceed to property loading phase.
     * This method <b>must</b> be invoked to finalize builder initialization phase,
     * and must always be the last method called during this phase.
     *
     * @return This builder instance.
     */
    @PhaseOnly(INITIALIZATION)
    @Finalizes(INITIALIZATION)
    public IOmniconfigBuilder loadFile();

    /**
     * Set prefix that all subsequently built config properties will have.
     * Own property name will be appended to this prefix.
     *
     * @param prefix Whatever prefix your want.
     * @return This builder instance.
     */
    @PhaseOnly(PROPERTY_LOADING)
    public IOmniconfigBuilder prefix(String prefix);

    /**
     * Remove prefix for subsequently built config properties.
     * Will result in own property name not being prefixed with anything.
     *
     * @return This builder instance.
     */
    @PhaseOnly(PROPERTY_LOADING)
    public IOmniconfigBuilder resetPrefix();

    /**
     * Put the specified category on top of current category stack.
     *
     * @param category Name of category to push.
     * @return This builder instance.
     * @see #pushCategory(String, String)
     */
    @PhaseOnly(PROPERTY_LOADING)
    public IOmniconfigBuilder pushCategory(String category);

    /**
     * Put specified category on top of current category stack.
     * You can create subcategory hierarchy of any complexity by pushing
     * and popping category names in desired order.<br/><br/>
     *
     * This method must be invoked at least once before building any properties;
     * if you attempt to create property builder with an empty category stack, an
     * {@link IllegalStateException} will be thrown.
     *
     * @param category Name of category to push.
     * @param comment Human-readable commentary on this category's purpose.
     * @return This builder instance.
     */
    @PhaseOnly(PROPERTY_LOADING)
    public IOmniconfigBuilder pushCategory(String category, String comment);

    /**
     * Pop category on top of current category stack.
     * @return This builder instance.
     */
    @PhaseOnly(PROPERTY_LOADING)
    public IOmniconfigBuilder popCategory();

    /**
     * Pop all current categories. Category stack will be left about as empty
     * and devastated as your soul have grown over last years.
     *
     * @return This builder instance.
     */
    @PhaseOnly(PROPERTY_LOADING)
    public IOmniconfigBuilder resetCategory();

    /**
     * If argument is true, ensures that every subsequently built properties
     * will be marked as synchronized by default; otherwise, ensures subsequently
     * build properties are not synchronized by default. In both cases that default
     * state can be overriden by invoking {@link IAbstractPropertyBuilder#sync(boolean)}
     * when building property itself.
     *
     * @param sync Default synchronized state for subsequent properties.
     * @return This builder instance.
     */
    @PhaseOnly(PROPERTY_LOADING)
    public IOmniconfigBuilder synchronize(boolean sync);

    /**
     * Start builder for {@link IBooleanProperty}.
     *
     * @param name Name of the property.
     * @param defaultValue Default value in config file.
     * @return Specialized sub-builder for <code>boolean</code> property.
     * @see IBooleanPropertyBuilder
     */
    @PhaseOnly(PROPERTY_LOADING)
    public IBooleanPropertyBuilder getBoolean(String name, boolean defaultValue);

    /**
     * Start builder for {@link IIntegerProperty}.
     *
     * @param name Name of the property.
     * @param defaultValue Default value in config file.
     * @return Specialized sub-builder for <code>int</code> property.
     * @see IIntegerPropertyBuilder
     */
    @PhaseOnly(PROPERTY_LOADING)
    public IIntegerPropertyBuilder getInteger(String name, int defaultValue);

    /**
     * Start builder for {@link IDoubleProperty}.
     *
     * @param name Name of the property.
     * @param defaultValue Default value in config file.
     * @return Specialized sub-builder for <code>double</code> property.
     * @see IDoubleProperty
     */
    @PhaseOnly(PROPERTY_LOADING)
    public IDoublePropertyBuilder getDouble(String name, double defaultValue);

    /**
     * Start builder for {@link IFloatProperty}.
     *
     * @param name Name of the property.
     * @param defaultValue Default value in config file.
     * @return Specialized sub-builder for <code>float</code> property.
     * @see IFloatProperty
     */
    @PhaseOnly(PROPERTY_LOADING)
    public IFloatPropertyBuilder getFloat(String name, float defaultValue);

    /**
     * Start builder for {@link IPerhapsProperty}.
     *
     * @param name Name of the property.
     * @param defaultValue Default value in config file.
     * @return Specialized sub-builder for {@link Perhaps} property.
     * @see IPerhapsProperty
     */
    @PhaseOnly(PROPERTY_LOADING)
    public IPerhapsPropertyBuilder getPerhaps(String name, Perhaps defaultValue);

    /**
     * Start builder for {@link IStringProperty}.
     *
     * @param name Name of the property.
     * @param defaultValue Default value in config file.
     * @return Specialized sub-builder for {@link String} property.
     * @see IStringProperty
     */
    @PhaseOnly(PROPERTY_LOADING)
    public IStringPropertyBuilder getString(String name, String defaultValue);

    /**
     * Start builder for {@link IStringListProperty}.
     *
     * @param name Name of the property.
     * @param defaultValue String array serving as default value in config file.
     * @return Specialized sub-builder for property containing list of {@link String}s.
     * @see IStringListProperty
     */
    @PhaseOnly(PROPERTY_LOADING)
    public IStringListPropertyBuilder getStringList(String name, String... defaultValue);

    /**
     * Start builder for {@link IEnumProperty}.
     *
     * @param <T> Type of specific enum this property will contain.
     * @param name Name of the property.
     * @param defaultValue Default value in config file.
     * @return Specialized sub-builder for {@link Enum} property.
     * @see IEnumProperty
     */
    @PhaseOnly(PROPERTY_LOADING)
    public <T extends Enum<T>> IEnumPropertyBuilder<T> getEnum(String name, T defaultValue);

    /**
     * Mark this particular config as reloadable.<br/>
     * If config is marked as reloadable, physical file it is associated with it
     * will be monitored for any changes at runtime. If such changes do occur, it will be
     * automatically attempted to re-load values from that file and update all property objects
     * constructed duing property loading phase accordingly to current file state.<br/><br/>
     *
     * This allows to apply user-made changes to physical file immediately after such changes
     * are made, thus letting these changes take effect without having them to re-start their
     * Minecraft client/server.
     *
     * @return This builder instance.
     */
    @PhaseOnly({PROPERTY_LOADING, FINALIZATION})
    @Finalizes(PROPERTY_LOADING)
    public IOmniconfigBuilder setReloadable();

    /**
     * If this config is marked as reloadable, passed consumer will be invoked every time
     * file change is detected, after it was attempted to re-load all properties from it.<br/>
     * Argument passed to consumer will be same {@link IOmniconfig} instance as returned by
     * {@link #build()}.<br/><br/>
     *
     * It is also possible that cosumer will be invoked for non-reloadable file, in case
     * {@link IOmniconfig#forceReload()} method is invoked. Normally this only happens when
     * client logs out of remote server, configuration is not sided and contains at least one
     * synchronized value; that is neccessary to drop server-forced properties and restore local
     * values.
     *
     * @param consumer Consumer to invoke.
     * @return This builder instance.
     */
    @PhaseOnly({PROPERTY_LOADING, FINALIZATION})
    @Finalizes(PROPERTY_LOADING)
    public IOmniconfigBuilder addUpdateListener(Consumer<IOmniconfig> consumer);

    /**
     * If you create property sub-builder during property loading phase, but never end up
     * calling its <code>build()</code> method, it will result in {@link IllegalStateException}
     * being thrown when calling {@link #build()} method of {@link IOmniconfigBuilder} itself.<br/>
     * However, since in some cases it might not be needed to retain actual property instances,
     * any amount of property sub-builders can be started without finishing previous ones;
     * that will not be considered an error state.<br/><br/>
     *
     * This method can be called at the end if initialization or during finalization phase,
     * and signifies that you allow {@link IOmniconfigBuilder} to build all unfinished property
     * sub-builders automatically, and hereby prevent raising exception from incomplete builders
     * remaining at the time of calling its {@link #build()} method.
     *
     * @return This builder instance.
     */
    @PhaseOnly({PROPERTY_LOADING, FINALIZATION})
    @Finalizes(PROPERTY_LOADING)
    public IOmniconfigBuilder buildIncompleteParameters();

    /**
     * Finishes building your omniconfig instance. It is when this method is called that physical
     * file associated with your config will be saved back to disc, including all corrections
     * that were made to properties during runtime, like applying bounds for numeric properties
     * or removing unretained properties from old file, if such file did indeed exist previously.
     * @return {@link IOmniconfig} instance.
     * @see IOmniconfig
     */
    @PhaseOnly({PROPERTY_LOADING, FINALIZATION})
    @Finalizes({PROPERTY_LOADING, FINALIZATION})
    public IOmniconfig build();

}

/**
 * Marks target method as such that can only be invoked during specific {@link BuildingPhase}
 * of {@link IOmniconfigBuilder}.
 *
 * @author Aizistral
 * @see IOmniconfigBuilder
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
@interface PhaseOnly {

    public BuildingPhase[] value();

}

/**
 * Marks target method as such that invoking it finalizes specified {@link BuildingPhase}s
 * of {@link IOmniconfigBuilder}.
 *
 * @author Aizistral
 * @see IOmniconfigBuilder
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
@interface Finalizes {

    public BuildingPhase[] value();

}