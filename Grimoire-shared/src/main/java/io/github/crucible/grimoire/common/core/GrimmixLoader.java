package io.github.crucible.grimoire.common.core;

import com.google.common.base.Objects;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import io.github.crucible.grimoire.common.GrimoireCore;
import io.github.crucible.grimoire.common.GrimoireInternals;
import io.github.crucible.grimoire.common.api.GrimoireAPI;
import io.github.crucible.grimoire.common.api.configurations.IMixinConfiguration;
import io.github.crucible.grimoire.common.api.events.configurations.GrimoireConfigsEvent;
import io.github.crucible.grimoire.common.api.grimmix.GrimmixController;
import io.github.crucible.grimoire.common.api.grimmix.IGrimmix;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.LoadingStage;
import io.github.crucible.grimoire.common.modules.ForceLoadController;
import io.github.crucible.omniconfig.api.lib.Either;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.service.MixinService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class GrimmixLoader {
    public static Pattern classFile = Pattern.compile("[^\\s\\$]+(\\$[^\\s]+)?\\.class$");
    public static final GrimmixLoader INSTANCE = new GrimmixLoader();

    protected final List<GrimmixContainer> containerList = new ArrayList<>();
    protected final List<GrimmixContainer> activeContainerList = new ArrayList<>();

    protected LoadingStage internalStage = LoadingStage.PRE_CONSTRUCTION;
    protected GrimmixContainer activeContainer = null;
    protected boolean finishedScan = false;

    public GrimmixLoader() {
        // NO-OP
    }

    public LoadingStage getInternalStage() {
        return this.internalStage;
    }

    @Nullable
    public GrimmixContainer getActiveContainer() {
        return this.activeContainer;
    }

    public List<GrimmixContainer> getAllContainers() {
        return ImmutableList.copyOf(this.containerList);
    }

    public List<IGrimmix> getAllActiveContainers() {
        return ImmutableList.copyOf(this.activeContainerList);
    }

    private boolean isClassFile(String fileName) {
        return classFile.matcher(fileName).matches();
    }

    private boolean isJson(String fileName) {
        String splitName = ((fileName.contains("/")) ?
                fileName.substring(fileName.lastIndexOf("/")).replace("/", "") : fileName);

        return splitName.endsWith(".json");
    }

    @Nullable
    private InputStream tryGetInputStream(ZipFile file, ZipEntry entry) {
        try {
            InputStream stream = file.getInputStream(entry);
            return stream;
        } catch (IOException ex) {
            Throwables.propagate(ex);
        }

        return null;
    }

    @Nullable
    private InputStream tryGetInputStream(File file) {
        try {
            InputStream stream = new FileInputStream(file);
            return stream;
        } catch (IOException ex) {
            Throwables.propagate(ex);
        }

        return null;
    }

    private List<GrimmixAnnotationVisitor.GrimmixCandidate> examineForAnnotations(File file, List<GrimmixAnnotationVisitor.GrimmixCandidate> candidates, List<String> configList, String recursivePath) {
        if (file.isDirectory()) {
            String newPath = recursivePath == null ? "" : (recursivePath + file.getName() + File.separator);

            for (File newFile : file.listFiles()) {
                this.examineForAnnotations(newFile, candidates, configList, newPath); // Do recursive kekw
            }
        } else if (file.getName().endsWith(".jar")) { // Its a jar man
            try {
                JarFile jar = new JarFile(file);

                for (ZipEntry ze : Collections.list(jar.entries())) {
                    if (this.isClassFile(ze.getName())) {
                        GrimmixAnnotationVisitor.GrimmixCandidate candidate = GrimmixAnnotationVisitor.examineClassForGrimmix(jar, ze);

                        if (candidate.validate()) {
                            candidates.add(candidate);
                        }
                    } else if (this.isJson(ze.getName())) {
                        DeserializedMixinJson json = GrimoireInternals.deserializeMixinConfiguration(() -> this.tryGetInputStream(jar, ze));

                        if (json.isValidConfiguration()) {
                            if (json.getForceLoadType() != null) {
                                ForceLoadController.addForcedConfiguration(json.getForceLoadType(), ze.getName());
                            } else {
                                configList.add(ze.getName());
                            }
                        }
                    }
                }

                jar.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (this.isClassFile(file.getName())) { // Its a .class file
            GrimmixAnnotationVisitor.GrimmixCandidate candidate = GrimmixAnnotationVisitor.examineClassForGrimmix(file);

            if (candidate.validate()) {
                candidates.add(candidate);
            }
        } else if (this.isJson(file.getName())) {
            DeserializedMixinJson json = GrimoireInternals.deserializeMixinConfiguration(() -> this.tryGetInputStream(file));
            String cfgPath = recursivePath + file.getName();

            if (json.isValidConfiguration()) {
                if (json.getForceLoadType() != null) {
                    ForceLoadController.addForcedConfiguration(json.getForceLoadType(), cfgPath);
                } else {
                    configList.add(cfgPath);
                }
            }
        }

        return candidates;
    }

    protected void seekGrimmixes(Collection<URL> paths, @Nullable LaunchClassLoader classLoader) {
        for (URL url : paths) {
            try {
                URI uri = url.toURI();

                if (!"file".equals(uri.getScheme()) || !new File(uri).exists()) {
                    continue;
                }

                File candidateFile = new File(url.toURI().getPath());
                Manifest manifest = null;
                boolean isDirectory = candidateFile.isDirectory();

                GrimoireCore.logger.info("Scanning {} {} for Grimmix controllers...", isDirectory ? "directory" : "file", uri.toString().replaceAll("%20", " "));

                if (isDirectory) {
                    File manifestFile = new File(candidateFile, "META-INF/MANIFEST.MF");

                    if (manifestFile.exists()) {
                        FileInputStream stream = new FileInputStream(manifestFile);
                        manifest = new Manifest(stream);
                        stream.close();
                    }
                } else if (candidateFile.getName().endsWith("jar")) {
                    JarFile jar = new JarFile(candidateFile);
                    manifest = jar.getManifest();
                    jar.close();
                }

                boolean isGrimoireGrimmix = false;
                if (manifest != null) {
                    String isGrimoireAttribute = manifest.getMainAttributes().getValue("IsThatYouGrimoire");
                    if (Objects.equal(isGrimoireAttribute, "Indeed")) {
                        isGrimoireGrimmix = true;
                    }
                }

                List<GrimmixAnnotationVisitor.GrimmixCandidate> candidateList = new ArrayList<>();
                List<String> configList = new ArrayList<>();

                this.examineForAnnotations(candidateFile, candidateList, configList, null);

                if (candidateList.size() > 0 && classLoader != null) {
                    boolean alreadyThere = false;

                    for (URL classURL : classLoader.getURLs()) {
                        if (url.equals(classURL)) {
                            alreadyThere = true;
                            break;
                        }
                    }

                    if (!alreadyThere) {
                        GrimoireCore.logger.info("Adding location: {} to java classpath, not there yet", url.toString().replaceAll("%20", " "));
                        classLoader.addURL(url);
                    }
                }

                for (GrimmixAnnotationVisitor.GrimmixCandidate candidate : candidateList) {
                    try {
                        Class<?> controllerClass = Class.forName(candidate.getClassName());

                        if (!GrimmixController.class.isAssignableFrom(controllerClass))
                            throw new RuntimeException("Invalid @Grimmix annotation target: " + candidate.getClassName() + ". All targets of @Grimmix annotation must have GrimmixController in superclasss hierarchy!");

                        Constructor<? extends GrimmixController> controllerConstructor = (Constructor<? extends GrimmixController>) controllerClass.getConstructor();
                        controllerConstructor.setAccessible(true);

                        GrimmixContainer container = new GrimmixContainer(candidateFile, controllerConstructor, new ArrayList<>(configList), isGrimoireGrimmix);
                        this.containerList.add(container);
                        this.activeContainerList.add(container);

                        GrimoireCore.logger.info("Sucessfully collected controller constructor: " + controllerConstructor);
                        GrimoireCore.logger.info("Configuration candidates for{} Grimmix {}: {}", isGrimoireGrimmix ? " integrated" : "", candidate.getClassName(), configList);
                    } catch (Exception ex) {
                        throw new RuntimeException("Failed to collect controller constructor: " + candidate.getClassName(), ex);
                    }
                }

            } catch (Exception e) {
                if (e instanceof RuntimeException)
                    throw new RuntimeException(e);
                else {
                    e.printStackTrace();
                }
            }
        }
    }

    public void scanForGrimmixes(@Nullable LaunchClassLoader classLoader, File... directories) {
        if (this.finishedScan)
            return;

        GrimoireCore.logger.info("Scanning for Grimmix controllers in following locations: ");

        if (classLoader != null) {
            GrimoireCore.logger.info("Location: Java Classpath");
        }
        for (File file : directories) {
            try {
                GrimoireCore.logger.info("Location: " + file.getCanonicalFile().toURI().toString().replaceAll("%20", " "));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        List<URL> classUrls = new ArrayList<>();

        if (classLoader != null) {
            classUrls = Lists.newArrayList(MixinService.getService().getClassProvider().getClassPath());
            this.seekGrimmixes(classUrls, null);
        }

        List<URL> fileURLs = new ArrayList<>();

        for (File dir : directories) {
            if (dir.exists() && dir.isDirectory()) {
                for (File mod : FileUtils.listFiles(dir, new String[]{"jar"}, true)) {
                    try {
                        URL fileURL = mod.getCanonicalFile().toURI().toURL();

                        if (!classUrls.contains(fileURL)) {
                            fileURLs.add(fileURL);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }

        if (fileURLs.size() > 0) {
            this.seekGrimmixes(fileURLs, classLoader);
        }

        this.finishedScan = true;
    }

    public void construct() {
        this.transition(LoadingStage.CONSTRUCTION);
    }

    public void validate() {
        this.transition(LoadingStage.VALIDATION, true);
    }

    public void buildRuntimeConfigs() {
        this.transition(LoadingStage.MIXIN_CONFIG_BUILDING);
    }

    public void coreLoad() {
        this.transition(LoadingStage.CORELOAD);
    }

    public void modLoad() {
        this.transition(LoadingStage.MODLOAD);
    }

    public void finish() {
        this.transition(LoadingStage.FINAL);
    }

    protected void transition(LoadingStage to) {
        this.transition(to, false, null);
    }

    protected void transition(LoadingStage to, boolean dropOnCancel) {
        this.transition(to, dropOnCancel, null);
    }

    protected void transition(LoadingStage to, boolean dropOnCancel, Consumer<GrimmixContainer> beforeEach) {
        if (this.internalStage.ordinal() + 1 != to.ordinal())
            throw new IllegalStateException("Cannot transition from " + this.internalStage + " to " + to);

        this.internalStage = to;

        List<IMixinConfiguration> preparedConfigurations = new ArrayList<>();

        this.activeContainerList.removeIf((container) -> {
            this.activeContainer = container;

            if (beforeEach != null) {
                beforeEach.accept(container);
            }

            boolean valid = container.transition(to);

            if (valid) {
                if (to.isConfigurationStage()) {
                    preparedConfigurations.addAll(container.prepareConfigurations(to.getAssociatedConfigurationType()));
                }
            }

            if (dropOnCancel && !valid) {
                container.invalidate();
                return true;
            } else
                return false;
        });

        this.activeContainer = null;

        if (to == LoadingStage.MIXIN_CONFIG_BUILDING) {
            ConfigBuildingManager.generateRuntimeConfigurations();
        } else if (to.isConfigurationStage()) {
            preparedConfigurations.addAll(MixinConfiguration.prepareUnclaimedConfigurations(to.getAssociatedConfigurationType()));

            if (preparedConfigurations.size() > 0) {
                GrimoireConfigsEvent.Pre event = new GrimoireConfigsEvent.Pre(preparedConfigurations, to);
                GrimoireAPI.EVENT_BUS.post(event);

                if (!event.isCanceled()) {
                    for (IMixinConfiguration config : event.getPreparedConfigurations()) {
                        ((MixinConfiguration)config).load();
                    }
                }

                GrimoireAPI.EVENT_BUS.post(new GrimoireConfigsEvent.Post(event.getPreparedConfigurations(), to));
            }
        }
    }

}
