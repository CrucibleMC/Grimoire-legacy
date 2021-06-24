package io.github.crucible.grimoire.common.core;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;

import io.github.crucible.grimoire.common.GrimoireCore;
import io.github.crucible.grimoire.common.api.GrimoireAPI;
import io.github.crucible.grimoire.common.api.events.grimmix.GrimmixConfigBuildingEvent;
import io.github.crucible.grimoire.common.api.events.grimmix.GrimmixCoreLoadEvent;
import io.github.crucible.grimoire.common.api.events.grimmix.GrimmixFinishLoadEvent;
import io.github.crucible.grimoire.common.api.events.grimmix.GrimmixModLoadEvent;
import io.github.crucible.grimoire.common.api.events.grimmix.GrimmixValidationEvent;
import io.github.crucible.grimoire.common.api.grimmix.Grimmix;
import io.github.crucible.grimoire.common.api.grimmix.GrimmixController;
import io.github.crucible.grimoire.common.api.grimmix.IGrimmix;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.LoadingStage;
import io.github.crucible.grimoire.common.api.mixin.ConfigurationType;
import io.github.crucible.grimoire.common.api.mixin.IMixinConfiguration;
import io.github.crucible.grimoire.common.events.grimmix.ConfigBuildingEvent;
import io.github.crucible.grimoire.common.events.grimmix.CoreLoadEvent;
import io.github.crucible.grimoire.common.events.grimmix.FinishLoadEvent;
import io.github.crucible.grimoire.common.events.grimmix.ModLoadEvent;
import io.github.crucible.grimoire.common.events.grimmix.ValidationEvent;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class GrimmixContainer implements Comparable<GrimmixContainer>, IGrimmix {
    protected final List<IMixinConfiguration> ownedConfigurations = new ArrayList<>();
    protected final String name, modid, version;
    protected final long priority;
    protected final File grimmixFile;
    protected final Constructor<? extends GrimmixController> constructor;
    protected final List<String> configCandidates;
    protected final boolean isGrimoireGrimmix;
    protected final boolean wasOnClasspath;

    protected LoadingStage loadingStage = LoadingStage.PRE_CONSTRUCTION;
    protected GrimmixController controller = null;

    protected boolean valid = true;

    public GrimmixContainer(File file, Constructor<? extends GrimmixController> constructor, List<String> configCandidates, boolean isGrimoire, boolean wasOnClasspath) {
        this.grimmixFile = file;
        this.constructor = constructor;
        this.configCandidates = configCandidates;
        this.isGrimoireGrimmix = isGrimoire;
        this.wasOnClasspath = wasOnClasspath;

        String name = "", modid = "", version = "";
        long priority = 0L;

        for (Annotation annotation : constructor.getDeclaringClass().getAnnotations()) {
            if (annotation.annotationType().equals(Grimmix.class)) {
                Grimmix grimmix = (Grimmix) annotation;
                modid = grimmix.id();
                name = grimmix.name();
                version = grimmix.version();
                priority = grimmix.priority();
            }
        }

        this.name = name.isEmpty() ? constructor.getDeclaringClass().getSimpleName() : name;
        this.modid = modid.isEmpty() ? name.toLowerCase() : modid;
        this.version = version.isEmpty() ? "1.0.0" : version;
        this.priority = priority;

        List<String> list = this.listClassesInPackage("io.github.crucible.grimoire.common.api");
        String pattern = "mixin.*Mixin*";
        pattern = pattern.replace(".", "\\.").replace("*", ".*");

        System.out.println("Strings that match [" + pattern + "]:");
        for (String str : list) {
            if (str.matches(pattern)) {
                System.out.println(str);
            }
        }
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
        return Collections.unmodifiableList(this.ownedConfigurations);
    }

    public boolean wasOnClasspath() {
        return this.wasOnClasspath;
    }

    @Override
    public boolean isGrimoireGrimmix() {
        return this.isGrimoireGrimmix;
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

    private void recursiveClassScan(File dir, List<String> classList, String recursivePath) {
        try {
            String path = recursivePath != null ? recursivePath : "";
            File[] files = dir.listFiles();

            if (files != null && files.length > 0) {
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".class") && !file.getName().equals("package-info.class")) {
                        classList.add(path + file.getName().replace(".class", "").replace("$", "."));
                    } else if (file.isDirectory()) {
                        this.recursiveClassScan(file, classList, path + file.getName() + ".");
                    }
                }
            }
        } catch (Exception ex) {
            Throwables.propagate(ex);
        }
    }

    public List<String> listClassesInPackage(String packageName) {
        List<String> classList = new ArrayList<>();

        try {
            if (this.grimmixFile.isFile() && this.grimmixFile.getName().endsWith(".jar")) {
                // TODO Test this part
                String packagePath = packageName.replace(".", "/");

                JarFile jar = new JarFile(this.grimmixFile);
                Enumeration<JarEntry> entries = jar.entries();

                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    if (entry.getName().startsWith(packagePath) && GrimmixLoader.classFile.matcher(entry.getName()).matches()) {
                        classList.add(entry.getName().replace(".class", "").replace("$", ".").replace("/", ".").replaceFirst(packageName.replace(".", "\\.") + "\\.", ""));
                    }
                }

                jar.close();
            } else if (this.grimmixFile.isDirectory()) {
                File packageDir = new File(this.grimmixFile, packageName.replace(".", File.separator));
                this.recursiveClassScan(packageDir, classList, null);
            }

            System.out.println("Listed classes: ");
            for (String lClass : classList) {
                System.out.println(lClass);
            }
        } catch (Exception ex) {
            Throwables.propagate(ex);
        }

        return classList;
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
                ValidationEvent event = new ValidationEvent(this);

                if (!GrimoireAPI.EVENT_BUS.post(new GrimmixValidationEvent(event))) {
                    this.controller.validateController(event);
                } else
                    return false;

                return !event.isCanceled();
            } else if (to == LoadingStage.MIXIN_CONFIG_BUILDING) {
                ConfigBuildingEvent event = new ConfigBuildingEvent(this);

                if (!GrimoireAPI.EVENT_BUS.post(new GrimmixConfigBuildingEvent(event))) {
                    this.controller.buildMixinConfigs(event);
                } else
                    return false;

                return !event.isCanceled();
            } else if (to == LoadingStage.CORELOAD) {
                CoreLoadEvent event = new CoreLoadEvent(this, this.configCandidates);

                if (!GrimoireAPI.EVENT_BUS.post(new GrimmixCoreLoadEvent(event))) {
                    this.controller.coreLoad(event);
                } else
                    return false;

                return !event.isCanceled();
            } else if (to == LoadingStage.MODLOAD) {
                ModLoadEvent event = new ModLoadEvent(this, this.configCandidates);

                if (!GrimoireAPI.EVENT_BUS.post(new GrimmixModLoadEvent(event))) {
                    this.controller.modLoad(event);
                } else
                    return false;

                return !event.isCanceled();
            } else if (to == LoadingStage.FINAL) {
                FinishLoadEvent event = new FinishLoadEvent(this);

                if (!GrimoireAPI.EVENT_BUS.post(new GrimmixFinishLoadEvent(event))) {
                    this.controller.finish(event);
                } else
                    return false;

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
    public String getID() {
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
