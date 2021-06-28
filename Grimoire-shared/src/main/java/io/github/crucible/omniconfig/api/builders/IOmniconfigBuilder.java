package io.github.crucible.omniconfig.api.builders;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Consumer;
import java.util.function.Function;

import io.github.crucible.omniconfig.api.core.SidedConfigType;
import io.github.crucible.omniconfig.api.OmniconfigAPI;
import io.github.crucible.omniconfig.api.core.IOmniconfig;
import io.github.crucible.omniconfig.api.core.VersioningPolicy;
import io.github.crucible.omniconfig.api.lib.Perhaps;
import io.github.crucible.omniconfig.api.lib.Version;
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
 * order described above, calling each of builder methods on particular phase it belongs to.<br/><br/>
 *
 * For that sake, methods of this interface are decored with {@link PhaseOnly} annotation, specifying
 * during which phases this method can be called.<br/><br/>
 *
 * If a method is decorated with {@link Finalizes}, it means that calling it explicitly ends building
 * phase specified as annotation value. While <code>INITIALIZATION</code> phase must be ended by
 * calling {@link #loadFile()} method, <code>PROPERTY_LOADING</code> phase doesn't have such method;
 * it can be ended either by calling any of the methods from <code>FINALIZATION</code> phase, all
 * of which are currently optional, or by right away calling {@link #build()} method, which can end
 * both <code>INITIALIZATION</code> and <code>FINALIZATION</code> phases.<br/><br/>
 *
 * After <b>FINALIZATION</b> phase is over, builder is automatically invalidated;
 * calling any of its methods in such case will result in {@link IllegalStateException} being thrown.
 *
 * @author Aizistral
 */

public interface IOmniconfigBuilder {

    @PhaseOnly(INITIALIZATION)
    public IOmniconfigBuilder versioningPolicy(VersioningPolicy policy);

    @PhaseOnly(INITIALIZATION)
    public IOmniconfigBuilder versioningPolicyBackflips(Function<Version, VersioningPolicy> determinator);

    @PhaseOnly(INITIALIZATION)
    public IOmniconfigBuilder terminateNonInvokedKeys(boolean terminate);

    @PhaseOnly(INITIALIZATION)
    @Finalizes(INITIALIZATION)
    public IOmniconfigBuilder loadFile();

    @PhaseOnly(PROPERTY_LOADING)
    public IOmniconfigBuilder prefix(String prefix);

    @PhaseOnly(PROPERTY_LOADING)
    public IOmniconfigBuilder resetPrefix();

    @PhaseOnly(PROPERTY_LOADING)
    public IOmniconfigBuilder pushCategory(String category);

    @PhaseOnly(PROPERTY_LOADING)
    public IOmniconfigBuilder pushCategory(String category, String comment);

    @PhaseOnly(PROPERTY_LOADING)
    public IOmniconfigBuilder popCategory();

    @PhaseOnly(PROPERTY_LOADING)
    public IOmniconfigBuilder resetCategory();

    @PhaseOnly(PROPERTY_LOADING)
    public IOmniconfigBuilder synchronize(boolean sync);

    @PhaseOnly(PROPERTY_LOADING)
    public IBooleanPropertyBuilder getBoolean(String name, boolean defaultValue);

    @PhaseOnly(PROPERTY_LOADING)
    public IIntegerPropertyBuilder getInteger(String name, int defaultValue);

    @PhaseOnly(PROPERTY_LOADING)
    public IDoublePropertyBuilder getDouble(String name, double defaultValue);

    @PhaseOnly(PROPERTY_LOADING)
    public IFloatPropertyBuilder getFloat(String name, float defaultValue);

    @PhaseOnly(PROPERTY_LOADING)
    public IPerhapsPropertyBuilder getPerhaps(String name, Perhaps defaultValue);

    @PhaseOnly(PROPERTY_LOADING)
    public IStringPropertyBuilder getString(String name, String defaultValue);

    @PhaseOnly(PROPERTY_LOADING)
    public IStringListPropertyBuilder getStringList(String name, String... defaultValue);

    @PhaseOnly(PROPERTY_LOADING)
    public <T extends Enum<T>> IEnumPropertyBuilder<T> getEnum(String name, T defaultValue);

    @PhaseOnly(FINALIZATION)
    @Finalizes(INITIALIZATION)
    public IOmniconfigBuilder setReloadable();

    @PhaseOnly(FINALIZATION)
    @Finalizes(INITIALIZATION)
    public IOmniconfigBuilder addUpdateListener(Consumer<IOmniconfig> consumer);

    @PhaseOnly(FINALIZATION)
    @Finalizes(INITIALIZATION)
    public IOmniconfigBuilder buildIncompleteParameters();

    @PhaseOnly(FINALIZATION)
    @Finalizes({INITIALIZATION, FINALIZATION})
    public IOmniconfig build();
}

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
@interface PhaseOnly {
    BuildingPhase[] value();
}

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
@interface Finalizes {
    BuildingPhase[] value();
}