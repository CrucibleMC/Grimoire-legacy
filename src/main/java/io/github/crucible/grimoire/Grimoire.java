package io.github.crucible.grimoire;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@IFMLLoadingPlugin.Name("Grimoire")
@IFMLLoadingPlugin.MCVersion("1.7.10")
public class Grimoire implements IFMLLoadingPlugin {

    public Grimoire() {
        MixinBootstrap.init();
        if (!(Grimoire.class.getClassLoader() instanceof LaunchClassLoader))
            throw new RuntimeException("Coremod plugin class was loaded by another classloader!");

        LaunchClassLoader classLoader = (LaunchClassLoader) Grimoire.class.getClassLoader();

        File dir = new File("grimoire");
        if (!dir.exists()) {
            System.out.println("[Grimoire] Creating mixins folder.");
            if (!dir.mkdirs()) System.err.println("[Grimoire] Unable to create the folder " + dir.getAbsolutePath());
        }

        File[] files;
        List<File> loadedFiles = new ArrayList<>();
        if ((files = dir.listFiles()) != null) {
            for (File mod : files) {
                if (mod.isDirectory() || !mod.canRead() || !mod.getName().endsWith(".jar")) continue;

                try {
                    ZipFile zipFile = new ZipFile(mod);
                    Enumeration<? extends ZipEntry> entries = zipFile.entries();
                    List<ZipEntry> mixinEntries = new ArrayList<>();

                    while (entries.hasMoreElements()) {
                        ZipEntry entry = entries.nextElement();
                        if (isMixinConfiguration(entry.getName())) {
                            mixinEntries.add(entry);
                        }
                    }

                    if (!mixinEntries.isEmpty()) {
                        classLoader.addURL(mod.toPath().toUri().toURL());
                        loadedFiles.add(mod);
                        mixinEntries.forEach(json -> Mixins.addConfiguration(json.getName()));
                    }

                } catch (Exception e) {
                    System.err.println("[Grimoire] Unable to load \'" + mod.getAbsolutePath() + "\'.");
                    e.printStackTrace();
                }
            }

            StringBuilder fileList = new StringBuilder();
            for (File file : loadedFiles) {
                if (fileList.length() > 0)
                    fileList.append(", ");
                fileList.append(file.getName());
            }
            System.out.println(String.format("[Grimoire] Loaded files %s","(" + loadedFiles.size() + "): " + fileList.toString()));

        }
    }

    private static boolean isMixinConfiguration(String name) {
        String splitName = name.substring(name.lastIndexOf("/")); // Caso o zip esteja usando o "\" esse zip está quebrado, já que o padrão é o "/".
        if (splitName.startsWith("mixin.") && splitName.endsWith(".json")) return true;
        if (splitName.startsWith("mixin-") && splitName.endsWith(".json")) return true;
        return splitName.endsWith("-mixin.json");
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
