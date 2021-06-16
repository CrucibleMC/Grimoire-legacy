package io.github.crucible.omniconfig.api.builders;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Consumer;
import java.util.function.Function;

import io.github.crucible.omniconfig.api.core.IOmniconfig;
import io.github.crucible.omniconfig.api.core.VersioningPolicy;
import io.github.crucible.omniconfig.api.lib.Perhaps;
import io.github.crucible.omniconfig.api.lib.Version;

public interface IOmniconfigBuilder {

    @PhaseOnly(BuildingPhase.INITIALIZATION)
    public IOmniconfigBuilder versioningPolicy(VersioningPolicy policy);

    @PhaseOnly(BuildingPhase.INITIALIZATION)
    public IOmniconfigBuilder versioningPolicyBackflips(Function<Version, VersioningPolicy> determinator);

    @PhaseOnly(BuildingPhase.INITIALIZATION)
    public IOmniconfigBuilder terminateNonInvokedKeys(boolean terminate);

    @PhaseOnly(BuildingPhase.INITIALIZATION)
    public IOmniconfigBuilder loadFile();

    @PhaseOnly(BuildingPhase.PARAMETER_LOADING)
    public IOmniconfigBuilder prefix(String prefix);

    @PhaseOnly(BuildingPhase.PARAMETER_LOADING)
    public IOmniconfigBuilder resetPrefix();

    @PhaseOnly(BuildingPhase.PARAMETER_LOADING)
    public IOmniconfigBuilder pushCategory(String category);

    @PhaseOnly(BuildingPhase.PARAMETER_LOADING)
    public IOmniconfigBuilder pushCategory(String category, String comment);

    @PhaseOnly(BuildingPhase.PARAMETER_LOADING)
    public IOmniconfigBuilder popCategory();

    @PhaseOnly(BuildingPhase.PARAMETER_LOADING)
    public IOmniconfigBuilder resetCategory();

    @PhaseOnly(BuildingPhase.PARAMETER_LOADING)
    public IOmniconfigBuilder synchronize(boolean sync);

    @PhaseOnly(BuildingPhase.PARAMETER_LOADING)
    public IBooleanPropertyBuilder getBoolean(String name, boolean defaultValue);

    @PhaseOnly(BuildingPhase.PARAMETER_LOADING)
    public IIntegerPropertyBuilder getInteger(String name, int defaultValue);

    @PhaseOnly(BuildingPhase.PARAMETER_LOADING)
    public IDoublePropertyBuilder getDouble(String name, double defaultValue);

    @PhaseOnly(BuildingPhase.PARAMETER_LOADING)
    public IFloatPropertyBuilder getFloat(String name, float defaultValue);

    @PhaseOnly(BuildingPhase.PARAMETER_LOADING)
    public IPerhapsPropertyBuilder getPerhaps(String name, Perhaps defaultValue);

    @PhaseOnly(BuildingPhase.PARAMETER_LOADING)
    public IStringPropertyBuilder getString(String name, String defaultValue);

    @PhaseOnly(BuildingPhase.PARAMETER_LOADING)
    public IStringListPropertyBuilder getStringList(String name, String... defaultValue);

    @PhaseOnly(BuildingPhase.PARAMETER_LOADING)
    public <T extends Enum<T>> IEnumPropertyBuilder<T> getEnum(String name, T defaultValue);

    @PhaseOnly(BuildingPhase.FINALIZATION)
    public IOmniconfigBuilder setReloadable();

    @PhaseOnly(BuildingPhase.FINALIZATION)
    public IOmniconfigBuilder addUpdateListener(Consumer<IOmniconfig> consumer);

    @PhaseOnly(BuildingPhase.FINALIZATION)
    public IOmniconfigBuilder buildIncompleteParameters();

    @PhaseOnly(BuildingPhase.FINALIZATION)
    public IOmniconfig build();
}

enum BuildingPhase {
    INITIALIZATION,
    PARAMETER_LOADING,
    FINALIZATION;
}

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
@interface PhaseOnly {
    BuildingPhase[] value();
}