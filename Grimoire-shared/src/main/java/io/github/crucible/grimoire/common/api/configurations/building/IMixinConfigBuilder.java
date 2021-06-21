package io.github.crucible.grimoire.common.api.configurations.building;

import org.spongepowered.asm.mixin.MixinEnvironment;

import io.github.crucible.grimoire.common.api.configurations.IMixinConfiguration;
import io.github.crucible.grimoire.common.api.configurations.IMixinConfiguration.ConfigurationType;
import io.github.crucible.grimoire.common.core.MixinConfigBuilder;

public interface IMixinConfigBuilder {

    public IMixinConfigBuilder targetEnvironment(MixinEnvironment.Phase envPhase);

    public IMixinConfigBuilder refmap(String refmap);

    public IMixinConfigBuilder required(boolean required);

    public IMixinConfigBuilder mixinPackage(String mixinPackage);

    public IMixinConfigBuilder commonMixins(String... mixinClasses);

    public IMixinConfigBuilder clientMixins(String... mixinClasses);

    public IMixinConfigBuilder serverMixins(String... mixinClasses);

    public IMixinConfigBuilder priority(int priority);

    public IMixinConfigBuilder mixinPriority(int priority);

    public IMixinConfigBuilder setSourceFile(boolean setOrNot);

    public IMixinConfigBuilder setConfigurationPlugin(String pluginClass);

    public IMixinConfigBuilder configurationType(ConfigurationType type);

    public IMixinConfiguration build();

}
