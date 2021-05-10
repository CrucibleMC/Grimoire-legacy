package io.github.crucible.grimoire.patch;

import java.io.File;
import java.util.List;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import javax.annotation.Nullable;

public class GrimPatch implements Comparable<GrimPatch> {

    private final long priority;
    private final String patchName;
    private final String modId;
    private final String targetJar;
    private final boolean corePatch;
    private final List<ZipEntry> mixinZipEntries;
    private final List<File> mixinFileEntries;
    private final String fileOrDirectoryName;

    public GrimPatch(Manifest manifest, List<ZipEntry> mixinZipEntries, File modFile) {
        this(manifest, mixinZipEntries, null, modFile);
    }

    public GrimPatch(Manifest manifest, File modFile, List<File> mixinFileEntries) {
        this(manifest, null, mixinFileEntries, modFile);
    }

    private GrimPatch(Manifest manifest, List<ZipEntry> mixinZipEntries, List<File> mixinFileEntries, File modFile) {
        this.priority       = this.getLong(manifest,"GRIMOIRE_PRIORITY", 0L);
        this.patchName      = this.getString(manifest,"GRIMOIRE_PATCHNAME", modFile.getName());
        this.modId          = this.getString(manifest,"GRIMOIRE_MODID", "");
        this.targetJar      = this.getString(manifest,"GRIMOIRE_TARGETJAR", "");
        this.corePatch      = this.getBoolean(manifest,"GRIMOIRE_COREPATCH", false);
        this.mixinZipEntries     = mixinZipEntries;
        this.mixinFileEntries    = mixinFileEntries;
        this.fileOrDirectoryName = modFile.getName();
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

    @Nullable
    public List<ZipEntry> getMixinZipEntries() {
        return this.mixinZipEntries;
    }

    @Nullable
    public List<File> getMixinFileEntries() {
        return this.mixinFileEntries;
    }

    private long getLong(Manifest manifest, String key, long def) {
        try {
            if (manifest != null)
                return Long.parseLong(manifest.getMainAttributes().getValue(key));
        } catch (Exception ignored) {
            // NO-OP
        }

        return def;
    }

    private boolean getBoolean(Manifest manifest, String key, boolean def) {
        try {
            if (manifest != null)
                return Boolean.parseBoolean(manifest.getMainAttributes().getValue(key));
        } catch (Exception ignored) {
            // NO-OP
        }

        return def;
    }

    private String getString(Manifest manifest, String key, String def) {
        try {
            if (manifest != null) {
                String targetValue = manifest.getMainAttributes().getValue(key);
                if (targetValue != null)
                    return targetValue;
            }
        }  catch (Exception ignored) {
            // NO-OP
        }

        return def;
    }

    public String getFileOrDirectoryName() {
        return this.fileOrDirectoryName;
    }

    @Override
    public int compareTo(GrimPatch other) {
        return Long.compare(this.priority, other.priority);
    }
}
