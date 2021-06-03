package io.github.crucible.grimoire.core;

import com.google.common.collect.ImmutableList;
import io.github.crucible.grimoire.Grimoire;
import io.github.crucible.grimoire.api.GrimoireAPI;
import io.github.crucible.grimoire.api.configurations.IMixinConfiguration;
import io.github.crucible.grimoire.api.configurations.events.MixinConfigurationEvent;
import io.github.crucible.grimoire.api.grimmix.IGrimmix;
import org.spongepowered.asm.mixin.Mixins;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MixinConfiguration implements IMixinConfiguration {
    private static final List<MixinConfiguration> unclaimedConfigurations = new ArrayList<>();
    private static boolean permitConfig = false;
    protected final Optional<IGrimmix> owner;
    protected final String classpath;
    protected final ConfigurationType configType;
    protected boolean isLoaded = false;
    protected boolean isValid = true;
    public MixinConfiguration(IGrimmix owner, ConfigurationType type, String classpath) {
        this.owner = owner != null ? Optional.of(owner) : Optional.empty();
        this.classpath = classpath;
        this.configType = type;

        MixinConfigurationEvent event = new MixinConfigurationEvent(owner, this);
        GrimoireAPI.EVENT_BUS.post(event);

        if (event.isCanceled()) {
            this.isValid = false;
            return;
        }

        if (this.owner.isPresent()) {
            ((GrimmixContainer) this.owner.get()).ownedConfigurations.add(this);
        } else {
            unclaimedConfigurations.add(this);
        }

        Grimoire.logger.info("Registered new IMixinConfiguration, owner: {}, type: {}, paths: {}",
                this.owner.isPresent() ? this.owner.get().getName() : null, this.configType, this.classpath);
    }

    public static List<IMixinConfiguration> getUnclaimedConfigurations() {
        return ImmutableList.copyOf(unclaimedConfigurations);
    }

    public static List<IMixinConfiguration> prepareUnclaimedConfigurations(ConfigurationType ofType) {
        List<IMixinConfiguration> configList = new ArrayList<>();

        for (IMixinConfiguration config : unclaimedConfigurations) {
            if (config.getConfigType() == ofType) {
                configList.add(config);
            }
        }

        return configList;
    }

    @Override
    public Optional<IGrimmix> getOwner() {
        return this.owner;
    }

    @Override
    public String getClasspath() {
        return this.classpath;
    }

    @Override
    public ConfigurationType getConfigType() {
        return this.configType;
    }

    @Override
    public boolean isLoaded() {
        return this.isLoaded;
    }

    @Override
    public boolean isValid() {
        return this.isValid;
    }

    @Override
    public void load() {
        if (!this.isLoaded && this.isValid) {
            this.isLoaded = true;
            permitConfig = true;

            Mixins.addConfiguration(this.classpath);

            permitConfig = false;
        }
    }

}
