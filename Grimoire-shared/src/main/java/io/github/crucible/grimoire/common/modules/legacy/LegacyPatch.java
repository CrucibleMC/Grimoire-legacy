package io.github.crucible.grimoire.common.modules.legacy;
import java.io.File;
import java.util.List;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

class LegacyPatch implements Comparable<LegacyPatch> {

    private final long priority;
    private final String patchName;
    private final String modId;
    private final String targetJar;
    private final boolean corePatch;
    private final List<ZipEntry> mixinEntries;
    private final String fileName;
    private final boolean wasOnClasspath;
    private final File file;

    public LegacyPatch(Manifest manifest, List<ZipEntry> mixinEntries, File modFile, boolean wasOnClasspath) {
        this.priority       = this.getLong(manifest,"GRIMOIRE_PRIORITY", 0L);
        this.patchName      = this.getString(manifest,"GRIMOIRE_PATCHNAME", modFile.getName());
        this.modId          = this.getString(manifest,"GRIMOIRE_MODID", "");
        this.targetJar      = this.getString(manifest,"GRIMOIRE_TARGETJAR", "");
        this.corePatch      = this.getBoolean(manifest,"GRIMOIRE_COREPATCH", false);
        this.mixinEntries   = mixinEntries;
        this.fileName       = modFile.getName();
        this.file           = modFile;
        this.wasOnClasspath = wasOnClasspath;
    }

    public boolean wasOnClasspath() {
        return this.wasOnClasspath;
    }

    public long getPriority() {
        return this.priority;
    }

    public String getPatchName() {
        return this.patchName;
    }

    public String getModId() {
        return this.modId;
    }

    public String getTargetJar() {
        return this.targetJar;
    }

    public boolean isCorePatch() {
        return this.corePatch;
    }

    public List<ZipEntry> getMixinEntries() {
        return this.mixinEntries;
    }

    private long getLong(Manifest manifest, String key, long def) {
        try {
            return Long.parseLong(manifest.getMainAttributes().getValue(key));
        } catch (Exception ignored) {
            // NO-OP
        }
        return def;
    }

    private boolean getBoolean(Manifest manifest, String key, boolean def) {
        try {
            return Boolean.parseBoolean(manifest.getMainAttributes().getValue(key));
        } catch (Exception ignored) {
            // NO-OP
        }
        return def;
    }

    private String getString(Manifest manifest, String key, String def) {
        try {
            String targetValue = manifest.getMainAttributes().getValue(key);
            return targetValue != null ? targetValue : def;
        } catch (Exception ignored) {
            // NO-OP
        }

        return def;
    }

    public String getFileName() {
        return this.fileName;
    }

    public File getFile() {
        return this.file;
    }

    @Override
    public int compareTo(LegacyPatch other) {
        return Long.compare(this.priority, other.priority);
    }
}