package io.github.crucible.grimoire.common.core;

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
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.service.MixinService;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;

import io.github.crucible.grimoire.common.GrimoireCore;
import io.github.crucible.grimoire.common.GrimoireInternals;
import io.github.crucible.grimoire.common.api.GrimoireAPI;
import io.github.crucible.grimoire.common.api.eventbus.CoreEvent;
import io.github.crucible.grimoire.common.api.eventbus.CoreEventBus;
import io.github.crucible.grimoire.common.api.eventbus.CoreEventHandler;
import io.github.crucible.grimoire.common.api.events.configurations.GrimoireConfigsEvent;
import io.github.crucible.grimoire.common.api.grimmix.GrimmixController;
import io.github.crucible.grimoire.common.api.grimmix.IGrimmix;
import io.github.crucible.grimoire.common.api.grimmix.lifecycle.LoadingStage;
import io.github.crucible.grimoire.common.api.mixin.ConfigurationType;
import io.github.crucible.grimoire.common.api.mixin.IMixinConfiguration;
import io.github.crucible.grimoire.common.core.GrimoireAnnotationAnalyzer.EventHandlerCandidate;
import io.github.crucible.grimoire.common.core.GrimoireAnnotationAnalyzer.GrimmixCandidate;
import io.github.crucible.grimoire.common.core.runtimeconfig.ConfigBuildingManager;
import io.github.crucible.grimoire.common.modules.ForceLoadController;
import net.minecraft.launchwrapper.LaunchClassLoader;

public class GrimmixLoader {
    public static final Pattern classFile = Pattern.compile("[^\\s\\$]+(\\$[^\\s]+)?\\.class$");
    public static final GrimmixLoader INSTANCE = new GrimmixLoader();

    protected final List<GrimmixContainer> containerList = new ArrayList<>();
    protected final List<GrimmixContainer> activeContainerList = new ArrayList<>();
    protected final List<Class<?>> staticEventHandlers = new ArrayList<>();
    protected final List<IMixinConfiguration> preparedConfigs = new ArrayList<>();

    protected LoadingStage internalStage = LoadingStage.PRE_CONSTRUCTION;
    protected GrimmixContainer activeContainer = null;
    protected boolean finishedScan = false;

    private List<File> scannedFiles = new ArrayList<>();

    public GrimmixLoader() {
        // NO-OP
    }

    public boolean isGrimmix(String fileName) {
        for (GrimmixContainer grimmix : this.containerList) {
            if (grimmix.getGrimmixFile().getName().equals(fileName) && !grimmix.wasOnClasspath())
                return true;
        }

        return false;
    }

    public LoadingStage getInternalStage() {
        return this.internalStage;
    }

    @Nullable
    public GrimmixContainer getActiveContainer() {
        return this.activeContainer;
    }

    public List<GrimmixContainer> getAllContainers() {
        return Collections.unmodifiableList(this.containerList);
    }

    public List<IGrimmix> getAllActiveContainers() {
        return Collections.unmodifiableList(this.activeContainerList);
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

    private boolean examineForAnnotations(File someFile, List<GrimmixCandidate> candidates, List<EventHandlerCandidate> handlers, List<String> configList, String recursivePath) {
        boolean hadForcedConfigurations = false;
        try {
            final File file = someFile.getCanonicalFile();

            if (file.isDirectory()) {
                String newPath = recursivePath == null ? "" : (recursivePath + file.getName() + File.separator);

                for (File newFile : file.listFiles()) {
                    this.examineForAnnotations(newFile, candidates, handlers, configList, newPath); // Do recursive kekw
                }
            } else if (file.getName().endsWith(".jar")) { // Its a jar man
                if (this.scannedFiles.contains(file))
                    return false;

                this.scannedFiles.add(file);

                JarFile jar = new JarFile(file);

                for (ZipEntry ze : Collections.list(jar.entries())) {
                    if (this.isClassFile(ze.getName())) {
                        GrimoireAnnotationAnalyzer analyzer = null;

                        try {
                            analyzer = GrimoireAnnotationAnalyzer.examineClass(jar, ze);
                        } catch (Exception ex) {
                            // Ignore, likely invalid class file
                        }

                        if (analyzer != null) {
                            if (analyzer.getGrimmixCandidate().validate()) {
                                candidates.add(analyzer.getGrimmixCandidate());
                            }

                            if (analyzer.getHandlerCandidate().validate()) {
                                handlers.add(analyzer.getHandlerCandidate());
                            }
                        }
                    } else if (this.isJson(ze.getName())) {
                        DeserializedMixinJson json = DeserializedMixinJson.deserialize(() -> this.tryGetInputStream(jar, ze));

                        if (json != null && json.isValidConfiguration()) {
                            if (json.getForceLoadType() != null) {
                                hadForcedConfigurations = true;
                                ForceLoadController.addForcedConfiguration(json.getForceLoadType(), ze.getName());
                            } else {
                                configList.add(ze.getName());
                            }
                        }
                    }
                }

                jar.close();
            } else if (this.isClassFile(file.getName())) { // Its a .class file
                GrimoireAnnotationAnalyzer analyzer = null;

                try {
                    analyzer = GrimoireAnnotationAnalyzer.examineClass(file);
                } catch (Exception ex) {
                    // Ignore, likely invalid class file
                }

                if (analyzer != null) {
                    if (analyzer.getGrimmixCandidate().validate()) {
                        candidates.add(analyzer.getGrimmixCandidate());
                    }

                    if (analyzer.getHandlerCandidate().validate()) {
                        handlers.add(analyzer.getHandlerCandidate());
                    }
                }
            } else if (this.isJson(file.getName())) {
                DeserializedMixinJson json = DeserializedMixinJson.deserialize(() -> this.tryGetInputStream(file));
                String cfgPath = recursivePath + file.getName();
                cfgPath = cfgPath.replace(File.separator, "/");

                if (json != null && json.isValidConfiguration()) {
                    if (json.getForceLoadType() != null) {
                        hadForcedConfigurations = true;
                        ForceLoadController.addForcedConfiguration(json.getForceLoadType(), cfgPath);
                    } else {
                        configList.add(cfgPath);
                    }
                }
            }
        } catch (Throwable ex) {
            Throwables.propagate(ex);
        }

        return hadForcedConfigurations;
    }

    private boolean arePathsEqual(URL one, URL two) {
        try {
            for (URL url : new URL[] { one, two }) {
                URI uri = url.toURI();

                if (!"file".equals(uri.getScheme()) || !new File(uri).exists())
                    return Objects.equals(one, two);
            }

            File fileOne = new File(one.toURI().getPath());
            File fileTwo = new File(two.toURI().getPath());

            if (Objects.equals(fileOne.getCanonicalFile(), fileTwo.getCanonicalFile()))
                return true;
            else
                return false;

        } catch (Exception ex) {
            return Objects.equals(one, two);
        }
    }

    protected void seekGrimmixes(Collection<URL> paths, @Nullable LaunchClassLoader classLoader, boolean ignoreFolders) {
        for (URL url : paths) {
            try {
                URI uri = url.toURI();

                if (!"file".equals(uri.getScheme()) || !new File(uri).exists()) {
                    continue;
                }

                File candidateFile = new File(url.toURI().getPath()).getCanonicalFile();
                Manifest manifest = null;
                boolean isDirectory = candidateFile.isDirectory();

                for (IGrimmix grimmix : this.getAllContainers()) {
                    if (Objects.equals(grimmix.getGrimmixFile(), candidateFile)) {
                        GrimoireCore.logger.info("Skipping {} {}, already scanned...", isDirectory ? "directory" : "file", GrimoireInternals.sanitizePath(uri.toString()));
                    }
                }

                GrimoireCore.logger.info("Scanning {} {} for grimmix controllers...", isDirectory ? "directory" : "file", GrimoireInternals.sanitizePath(uri.toString()));

                if (!candidateFile.canRead()) {
                    continue;
                }

                if (isDirectory) {
                    if (ignoreFolders) {
                        continue;
                    }

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
                    if (Objects.equals(isGrimoireAttribute, "Indeed")) {
                        isGrimoireGrimmix = true;
                    }
                }

                List<GrimmixCandidate> candidateList = new ArrayList<>();
                List<EventHandlerCandidate> handlerList = new ArrayList<>();
                List<String> configList = new ArrayList<>();

                boolean hadForcedConfigurations = this.examineForAnnotations(candidateFile, candidateList, handlerList, configList, null);
                boolean alreadyThere = false;

                if (classLoader != null && !candidateFile.isDirectory())
                    if (candidateList.size() > 0 || handlerList.size() > 0 || hadForcedConfigurations) {
                        for (URL classURL : classLoader.getURLs()) {
                            if (this.arePathsEqual(url, classURL)) {
                                alreadyThere = true;
                                break;
                            }
                        }

                        if (!alreadyThere) {
                            GrimoireCore.logger.info("Adding location: {} to java classpath, not there yet", url.toString().replaceAll("%20", " "));
                            classLoader.addURL(url);
                            GrimoireCore.INSTANCE.getForcedFilenames().add(candidateFile.getName());
                        }
                    }

                for (GrimmixCandidate candidate : candidateList) {
                    try {
                        Class<?> controllerClass = Class.forName(candidate.getClassName());

                        if (!GrimmixController.class.isAssignableFrom(controllerClass))
                            throw new RuntimeException("Invalid @Grimmix annotation target: " + candidate.getClassName() + ". All targets of @Grimmix annotation must have GrimmixController in superclasss hierarchy!");

                        Constructor<? extends GrimmixController> controllerConstructor = (Constructor<? extends GrimmixController>) controllerClass.getConstructor();
                        controllerConstructor.setAccessible(true);

                        GrimmixContainer container = new GrimmixContainer(candidateFile, controllerConstructor, new ArrayList<>(configList), isGrimoireGrimmix, alreadyThere);
                        this.containerList.add(container);
                        this.activeContainerList.add(container);

                        GrimoireCore.logger.info("Configuration candidates for {} Grimmix {}: {}", isGrimoireGrimmix ? "integrated" : "external", candidate.getClassName(), configList);
                    } catch (Exception ex) {
                        throw new RuntimeException("Failed to collect controller constructor: " + candidate.getClassName(), ex);
                    }
                }

                if (handlerList.size() > 0) {
                    handlerList.forEach(candidate -> {
                        try {
                            Class<?> handlerClass = Class.forName(candidate.getClassName());
                            this.staticEventHandlers.add(handlerClass);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });
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

        GrimoireCore.logger.info("Scanning for @Grimmix controllers in following locations: ");

        if (classLoader != null) {
            GrimoireCore.logger.info("Location: Java Classpath");
        }
        for (File file : directories) {
            try {
                GrimoireCore.logger.info("Location: " + GrimoireInternals.sanitizePath(file.getCanonicalFile().toURI().toString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        List<URL> classUrls = new ArrayList<>();

        if (classLoader != null) {
            classUrls = Lists.newArrayList(MixinService.getService().getClassProvider().getClassPath());
            this.seekGrimmixes(classUrls, null, false);
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
            this.seekGrimmixes(fileURLs, classLoader, true);
        }

        for (Class<?> handlerClass : this.staticEventHandlers) {
            CoreEventHandler annotation = handlerClass.getAnnotation(CoreEventHandler.class);

            if (annotation != null) {
                String[] busNames = annotation.value();

                for (String busName : busNames) {
                    Optional<CoreEventBus<? extends CoreEvent>> maybeBus = CoreEventBus.findBus(busName);

                    if (!maybeBus.isPresent() && annotation.mandatory())
                        throw new NoSuchElementException("Could not locate EventBus with name: " + busName
                                + ", required by static handler: " + handlerClass);

                    maybeBus.ifPresent(bus -> {
                        bus.register(handlerClass);
                        GrimoireCore.logger.info("Successfully subscribed annotated static handler {} to event bus {}", handlerClass, busName);
                    });
                }
            }

        }

        this.finishedScan = true;

        GrimoireCore.logger.info("Total of {} jar files was scanned.", this.scannedFiles.size());
        this.scannedFiles.clear();
        this.scannedFiles = null;
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

    public void prepareCoreConfigs() {
        this.transition(LoadingStage.CORELOAD);
    }

    public void loadCoreConfigs() {
        this.loadConfigs(ConfigurationType.CORE);
    }

    public void prepareModConfigs() {
        this.transition(LoadingStage.MODLOAD);
    }

    public void loadModConfigs() {
        this.loadConfigs(ConfigurationType.MOD);
    }

    private void loadConfigs(ConfigurationType type) {
        GrimoireConfigsEvent.Pre event = new GrimoireConfigsEvent.Pre(this.preparedConfigs, type.getAssociatedLoadingStage());
        GrimoireAPI.EVENT_BUS.post(event);

        if (!event.isCanceled()) {
            for (IMixinConfiguration config : event.getPreparedConfigurations()) {
                ((MixinConfiguration)config).load();
            }
        }

        GrimoireAPI.EVENT_BUS.post(new GrimoireConfigsEvent.Post(event.getPreparedConfigurations(), type.getAssociatedLoadingStage()));
        this.preparedConfigs.clear();
    }

    public void finish() {
        this.transition(LoadingStage.FINAL);
    }

    public List<IMixinConfiguration> getPreparedConfigs() {
        return Collections.unmodifiableList(this.preparedConfigs);
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

        GrimoireCore.logger.info("Transitioning from loading stage {} to stage {}...", this.internalStage, to);

        this.internalStage = to;
        this.preparedConfigs.clear();

        this.activeContainerList.removeIf((container) -> {
            this.activeContainer = container;

            if (beforeEach != null) {
                beforeEach.accept(container);
            }

            boolean valid = container.transition(to);

            if (valid) {
                if (to.isConfigurationStage()) {
                    this.preparedConfigs.addAll(container.prepareConfigurations(to.getAssociatedConfigurationType()));
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
            this.preparedConfigs.addAll(MixinConfiguration.prepareUnclaimedConfigurations(to.getAssociatedConfigurationType()));
            GrimoireCore.logger.info("Registered total of {} mixin configurations of type {}.", this.preparedConfigs.size(), to.getAssociatedConfigurationType());
        }

        GrimoireCore.logger.info("Sucessfully finished transition into {} loading stage.", this.internalStage);
    }

}
