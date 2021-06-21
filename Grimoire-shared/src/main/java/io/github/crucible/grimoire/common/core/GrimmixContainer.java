package io.github.crucible.grimoire.common.core;

import com.gamerforea.eventhelper.config.ConfigBoolean;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import io.github.crucible.grimoire.common.api.GrimoireAPI;
import io.github.crucible.grimoire.common.api.configurations.IMixinConfiguration;
import io.github.crucible.grimoire.common.api.grimmix.Grimmix;
import io.github.crucible.grimoire.common.api.grimmix.GrimmixController;
import io.github.crucible.grimoire.common.api.grimmix.IGrimmix;
import io.github.crucible.grimoire.common.api.grimmix.events.GrimmixConfigBuildingEvent;
import io.github.crucible.grimoire.common.api.grimmix.events.GrimmixCoreLoadEvent;
import io.github.crucible.grimoire.common.api.grimmix.events.GrimmixFinishLoadEvent;
import io.github.crucible.grimoire.common.api.grimmix.events.GrimmixModLoadEvent;
import io.github.crucible.grimoire.common.api.grimmix.events.GrimmixValidationEvent;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.LoadingStage;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
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

    protected LoadingStage loadingStage = LoadingStage.PRE_CONSTRUCTION;
    protected GrimmixController controller = null;

    protected boolean valid = true;

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

        List<String> list = this.listClassesInPackage("io.github.crucible.grimoire.common.api");
        String pattern = "configurations.*Mixin*";
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

    public List<IMixinConfiguration> prepareConfigurations(IMixinConfiguration.ConfigurationType ofType) {
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
                        classList.add(entry.getName().replace(".class", "").replace("$", ".").replace("/", ".").replaceFirst(packageName.replace(".", "\\."), ""));
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
                GrimmixValidationEvent event = new GrimmixValidationEvent();
                if (!GrimoireAPI.EVENT_BUS.post(event)) {
                    this.controller.validateController(event);
                }

                return !event.isCanceled();
            } else if (to == LoadingStage.MIXIN_CONFIG_BUILDING) {
                GrimmixConfigBuildingEvent event = new GrimmixConfigBuildingEvent();
                if (!GrimoireAPI.EVENT_BUS.post(event)) {
                    this.controller.buildMixinConfigs(event);
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
