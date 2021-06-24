package io.github.crucible.omniconfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import com.google.common.base.Throwables;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.github.crucible.grimoire.common.GrimoireCore;
import io.github.crucible.grimoire.common.GrimoireInternals;
import io.github.crucible.omniconfig.backing.Configuration;
import io.github.crucible.omniconfig.core.Omniconfig;

public class OmniconfigCore {
    public static final Logger logger = LogManager.getLogger("Omniconfig");
    public static final int STANDART_INTEGER_LIMIT = 32768;
    public static final String FILE_SEPARATOR = File.separator;
    public static final File CONFIG_DIR = new File(GrimoireCore.INSTANCE.getMCLocation(), "config");

    /**
     * This must only ever true if we are in a client environment and
     * currently are logged in to non-local server.
     */
    public static boolean onRemoteServer = false;

    public static final OmniconfigCore INSTANCE = new OmniconfigCore();

    // TODO Remove .zip extension once we're finished with extensive testing
    private final File defaultConfigsArchive = new File(GrimoireCore.INSTANCE.getDataFolder(), "defaultconfigs.zip");
    private final File defaultConfigsJson = new File(GrimoireCore.INSTANCE.getDataFolder(), "defaultconfigs.json");

    private OmniconfigCore() {
        // NO-OP
    }

    public String sanitizeName(String configName) {
        String newName = configName.replace("/", FILE_SEPARATOR);

        if (newName.endsWith(".cfg")) {
            newName = newName.substring(0, newName.length()-4);
        }

        return newName;
    }

    public void backUpDefaultCopy(Omniconfig cfg) {
        Configuration backingConfig = cfg.getBackingConfig();

        if (this.defaultConfigsArchive.exists() && this.defaultConfigsArchive.isFile()) {
            if (!this.compareMD5()) {
                logger.info("Deleting defaultconfigs archive...");
                this.defaultConfigsArchive.delete();
            }
        }

        if (!this.defaultConfigsArchive.exists() || !this.defaultConfigsArchive.isFile()) {
            try {
                ZipOutputStream out = new ZipOutputStream(new FileOutputStream(this.defaultConfigsArchive));
                out.close();

                this.updateMemorizedMD5Digest();
                logger.info("Made zip archive: " + this.defaultConfigsArchive.getCanonicalPath() + ", exists: " + this.defaultConfigsArchive.exists());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            File trueConfig = cfg.getFile();
            File tempConfig = File.createTempFile(trueConfig.getName(), null);
            backingConfig.setFile(tempConfig);
            backingConfig.forceDefault(true);
            backingConfig.save();

            this.updateFileWithinArchive(this.defaultConfigsArchive, cfg.getFile(), cfg.getFileID().replace(OmniconfigCore.FILE_SEPARATOR, "/"));

            tempConfig.delete();
            backingConfig.setFile(trueConfig);
            backingConfig.forceDefault(false);
        } catch (IOException e) {
            Throwables.propagate(e);
        }

        logger.info("Updated default copy of omniconfig file: {}", cfg.getFileID());
    }

    private void updateFileWithinArchive(File zipFile, File fileToUpdate, String zipEntryName) throws IOException {
        File tempFile = File.createTempFile(zipFile.getName(), null);
        tempFile.delete();

        // For the time of writing, preserve old archive as temporary file
        boolean renameOk = zipFile.renameTo(tempFile);
        if (!renameOk)
            throw new RuntimeException("Could not rename the file " + zipFile.getAbsolutePath() + " to " + tempFile.getAbsolutePath());
        byte[] buf = new byte[1024];

        ZipInputStream zipInput = new ZipInputStream(new FileInputStream(tempFile));
        ZipOutputStream zipOutput = new ZipOutputStream(new FileOutputStream(zipFile));

        // Copy all files from the old archive, besides that one we're trying to update
        ZipEntry entry = zipInput.getNextEntry();
        while (entry != null) {
            String entryName = entry.getName();

            if (zipEntryName.equals(entryName)) {
                entry = zipInput.getNextEntry();
                continue;
            }

            zipOutput.putNextEntry(new ZipEntry(entryName));

            int len;
            while ((len = zipInput.read(buf)) > 0) {
                zipOutput.write(buf, 0, len);
            }

            entry = zipInput.getNextEntry();
        }

        zipInput.close();

        // Now write whatever we're updating into the archive
        InputStream updateInput = new FileInputStream(fileToUpdate);
        zipOutput.putNextEntry(new ZipEntry(zipEntryName));

        int len;
        while ((len = updateInput.read(buf)) > 0) {
            zipOutput.write(buf, 0, len);
        }

        zipOutput.closeEntry();
        updateInput.close();

        // Finalize
        zipOutput.close();
        tempFile.delete();

        this.updateMemorizedMD5Digest();
    }

    private boolean compareMD5() {
        String memorized = this.getMemorizedMD5Digest();
        String current = this.getArchiveMD5Digest();

        boolean equals = Objects.equals(memorized, current);

        if (!equals) {
            logger.warn("Memorized md5 of defaultconfigs archive is different from that of the current file.");
            logger.warn("Last known: {}, current: {}", memorized, current);
        } else {
            logger.info("Memorized md5 of defaultconfigs archive matches that of current file: {}", memorized);
        }

        return equals;
    }

    private String getArchiveMD5Digest() {
        return GrimoireInternals.getMD5Digest(this.defaultConfigsArchive);
    }

    @Nullable
    private String getMemorizedMD5Digest() {
        try {
            if (this.defaultConfigsArchive.exists() && this.defaultConfigsArchive.isFile())
                if (this.defaultConfigsJson.exists() && this.defaultConfigsJson.isFile()) {
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    FileInputStream fileInput = new FileInputStream(this.defaultConfigsJson);
                    InputStreamReader streamReader = new InputStreamReader(fileInput, StandardCharsets.UTF_8);

                    HashMap<String, String> map = gson.fromJson(streamReader, HashMap.class);
                    String hash = map.get("md5");

                    streamReader.close();
                    fileInput.close();

                    return hash;
                }
        } catch (Exception ex) {
            Throwables.propagate(ex);
        }

        return null;
    }

    private void updateMemorizedMD5Digest() {
        this.updateMemorizedMD5Digest(this.getArchiveMD5Digest());
    }

    private void updateMemorizedMD5Digest(String hash) {
        try {
            FileOutputStream fileOutput = new FileOutputStream(this.defaultConfigsJson);
            OutputStreamWriter streamWriter = new OutputStreamWriter(fileOutput, StandardCharsets.UTF_8);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            HashMap<String, String> map = new HashMap<>();
            map.put("md5", hash);

            gson.toJson(map, streamWriter);

            streamWriter.close();
            fileOutput.close();

            logger.info("Updated memorized defaultconfigs md5 hash: {}", hash);
        } catch (Exception ex) {
            Throwables.propagate(ex);
        }
    }

    @Nullable
    public File extractDefaultCopy(String fileID) {
        try {
            if (this.defaultConfigsArchive.exists() && this.defaultConfigsArchive.isFile()) {
                if (this.compareMD5()) {
                    File defaultCopy = File.createTempFile(UUID.randomUUID().toString(), null);
                    defaultCopy.delete();

                    ZipFile archive = new ZipFile(this.defaultConfigsArchive);
                    ZipEntry entry = archive.getEntry(fileID.replace(OmniconfigCore.FILE_SEPARATOR, "/"));

                    if (entry != null) {
                        InputStream inputStream = archive.getInputStream(entry);
                        Files.copy(inputStream, defaultCopy.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        inputStream.close();
                    }

                    archive.close();

                    return defaultCopy;
                } else {
                    logger.info("Deleting defaultconfigs archive...");
                    this.defaultConfigsArchive.delete();
                    return null;
                }
            }
        } catch (Exception ex) {
            Throwables.propagate(ex);
        }

        return null;
    }

}
