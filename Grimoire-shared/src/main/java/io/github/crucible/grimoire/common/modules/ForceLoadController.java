package io.github.crucible.grimoire.common.modules;

import java.util.Map.Entry;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import io.github.crucible.grimoire.common.GrimoireCore;
import io.github.crucible.grimoire.common.api.grimmix.Grimmix;
import io.github.crucible.grimoire.common.api.grimmix.GrimmixController;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.ICoreLoadEvent;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.IFinishLoadEvent;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.IModLoadEvent;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.IValidationEvent;
import io.github.crucible.grimoire.common.api.mixin.IMixinConfiguration.ConfigurationType;

@Grimmix(id = "GrimoireForceLoadModule", name = "Grimoire Force Load Module")
public class ForceLoadController extends GrimmixController {
    private static final Multimap<ConfigurationType, String> forcedConfigurations = HashMultimap.create();
    private static boolean initialized = false;

    public ForceLoadController() {
        super();
    }

    @Override
    public void validateController(IValidationEvent event) {
        // TODO Maybe config option to disable this module
        initialized = true;

        GrimoireCore.logger.info("Grand total of {} forced mixin configuration was detected. List goes as following: ", forcedConfigurations.size());
        for (Entry<ConfigurationType, String> entry : forcedConfigurations.entries()) {
            GrimoireCore.logger.info("Path: {}, type: {}", entry.getValue(), entry.getKey());
        }
    }

    @Override
    public void coreLoad(ICoreLoadEvent event) {
        for (String classpath : forcedConfigurations.get(ConfigurationType.CORE)) {
            event.registerConfiguration(classpath);
        }
    }

    @Override
    public void modLoad(IModLoadEvent event) {
        for (String classpath : forcedConfigurations.get(ConfigurationType.MOD)) {
            event.registerConfiguration(classpath);
        }
    }

    @Override
    public void finish(IFinishLoadEvent event) {
        forcedConfigurations.clear();
    }

    public static void addForcedConfiguration(ConfigurationType type, String classpath) {
        if (initialized)
            return;

        forcedConfigurations.put(type, classpath);
    }

}