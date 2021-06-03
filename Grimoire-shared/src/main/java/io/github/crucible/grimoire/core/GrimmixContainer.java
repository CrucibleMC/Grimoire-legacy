package io.github.crucible.grimoire.core;

import com.google.common.collect.ImmutableList;
import io.github.crucible.grimoire.api.GrimoireAPI;
import io.github.crucible.grimoire.api.configurations.IMixinConfiguration;
import io.github.crucible.grimoire.api.configurations.IMixinConfiguration.ConfigurationType;
import io.github.crucible.grimoire.api.grimmix.Grimmix;
import io.github.crucible.grimoire.api.grimmix.GrimmixController;
import io.github.crucible.grimoire.api.grimmix.IGrimmix;
import io.github.crucible.grimoire.api.grimmix.events.GrimmixCoreLoadEvent;
import io.github.crucible.grimoire.api.grimmix.events.GrimmixFinishLoadEvent;
import io.github.crucible.grimoire.api.grimmix.events.GrimmixModLoadEvent;
import io.github.crucible.grimoire.api.grimmix.events.GrimmixValidationEvent;
import io.github.crucible.grimoire.api.grimmix.lifecycle.LoadingStage;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class GrimmixContainer implements Comparable<GrimmixContainer>, IGrimmix {
    protected final List<IMixinConfiguration> ownedConfigurations = new ArrayList<>();
    protected final String name, modid, version;
    protected final long priority;
    protected final File grimmixFile;
    protected final Constructor<? extends GrimmixController> constructor;
    protected final List<String> configCandidates;

    protected LoadingStage loadingStage = LoadingStage.PRE_CONSTRUCTION;
    protected GrimmixController controller = null;
    protected boolean valid = true;

    //protected List<>

    public GrimmixContainer(File file, Constructor<? extends GrimmixController> constructor, List<String> configCandidates) {
        this.grimmixFile = file;
        this.constructor = constructor;
        this.configCandidates = configCandidates;

        String name = "", modid = "", version = "";
        long priority = 0L;

        for (Annotation annotation : constructor.getDeclaringClass().getAnnotations()) {
            if (annotation.annotationType().equals(Grimmix.class)) {
                Grimmix grimmix = (Grimmix) annotation;
                modid = grimmix.modid();
                name = grimmix.name();
                version = grimmix.version();
                priority = grimmix.priority();
            }
        }

        this.name = name.isEmpty() ? constructor.getDeclaringClass().getSimpleName() : name;
        this.modid = modid.isEmpty() ? name.toLowerCase() : modid;
        this.version = version.isEmpty() ? "1.0.0" : version;
        this.priority = priority;
    }

    protected void constructController() {
        try {
            this.controller = this.constructor.newInstance();
        } catch (Exception ex) {
            throw new RuntimeException("Failed to construct Grimmix instance of class: " + this.constructor.getDeclaringClass(), ex);
        }
    }

    @Override
    public List<IMixinConfiguration> getOwnedConfigurations() {
        return ImmutableList.copyOf(this.ownedConfigurations);
    }

    @Override
    public File getGrimmixFile() {
        return this.grimmixFile;
    }

    public void invalidate() {
        this.valid = false;
    }

    @Override
    public boolean isValid() {
        return this.valid;
    }

    public List<IMixinConfiguration> prepareConfigurations(ConfigurationType ofType) {
        List<IMixinConfiguration> coreConfigs = new ArrayList<>();

        for (IMixinConfiguration config : this.ownedConfigurations) {
            if (config.getConfigType() == ofType) {
                coreConfigs.add(config);
            }
        }

        return coreConfigs;
    }

    public boolean transition(LoadingStage to) {
        if (!this.isValid())
            return false;

        if (this.loadingStage.isNextStage(to)) {
            this.loadingStage = to;

            if (to == LoadingStage.CONSTRUCTION) {
                this.constructController();
                return true;
            } else if (to == LoadingStage.VALIDATION) {
                GrimmixValidationEvent event = new GrimmixValidationEvent();
                if (!GrimoireAPI.EVENT_BUS.post(event)) {
                    this.controller.validateController(event);
                }

                return !event.isCanceled();
            } else if (to == LoadingStage.CORELOAD) {
                GrimmixCoreLoadEvent event = new GrimmixCoreLoadEvent(this.configCandidates);
                if (!GrimoireAPI.EVENT_BUS.post(event)) {
                    this.controller.coreLoad(event);
                }

                return !event.isCanceled();
            } else if (to == LoadingStage.MODLOAD) {
                GrimmixModLoadEvent event = new GrimmixModLoadEvent(this.configCandidates);
                if (!GrimoireAPI.EVENT_BUS.post(event)) {
                    this.controller.modLoad(event);
                }

                return !event.isCanceled();
            } else if (to == LoadingStage.FINAL) {
                GrimmixFinishLoadEvent event = new GrimmixFinishLoadEvent();
                if (!GrimoireAPI.EVENT_BUS.post(event)) {
                    this.controller.finish(event);
                }

                return !event.isCanceled();
            }

            return false;

        } else
            throw new IllegalArgumentException("Impossible to transition " + this.modid + " container from stage " + this.loadingStage + " to stage " + to);
    }

    @Override
    public LoadingStage getLoadingStage() {
        return this.loadingStage;
    }

    public GrimmixController getController() {
        return this.controller;
    }

    @Override
    public String getModID() {
        return this.modid;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public long getPriority() {
        return this.priority;
    }

    @Override
    public String getVersion() {
        return this.version;
    }

    @Override
    public int compareTo(GrimmixContainer other) {
        return Long.compare(this.getPriority(), other.getPriority());
    }

}
