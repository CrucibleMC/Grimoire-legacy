package io.github.crucible.grimoire;

import net.minecraft.launchwrapper.LaunchClassLoader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * list resources available from the classpath @ *
 */
public final class ResourceList {
    private ResourceList() {

    }
    /**
     * for all elements of the classloader urls get a Collection of resources matching the pattern
     * pattern = Pattern.compile(".*"); gets all resources
     *
     * @param pattern the pattern to match
     * @return the resources in the order they are found
     */
    public static Collection<String> getResources(Pattern pattern, URLClassLoader classLoader) throws IOException {
        ArrayList<String> found = new ArrayList<>();
        for (URL source : classLoader.getURLs()) {
            found.addAll(getResources(source.getPath(), pattern));
        }
        return found;
    }

    private static Collection<String> getResources(String element, Pattern pattern) throws IOException {
        ArrayList<String> found = new ArrayList<>();
        File file = new File(element);

        if (file.isDirectory()) {
            found.addAll(getResourcesFromDirectory(file, pattern));
        } else {
            found.addAll(getResourcesFromJarFile(file, pattern));
        }

        return found;
    }

    private static Collection<String> getResourcesFromJarFile(File file, Pattern pattern) throws IOException {
        ArrayList<String> found = new ArrayList<>();
        
        try (ZipFile zf = new ZipFile(file)) {
            final Enumeration<? extends ZipEntry> e = zf.entries();
            while (e.hasMoreElements()) {
                final ZipEntry ze = e.nextElement();
                final String fileName = ze.getName();
                final boolean accept = pattern.matcher(fileName).matches();
                if (accept) {
                    found.add(fileName);
                }
            }
        }

        return found;
    }

    private static Collection<String> getResourcesFromDirectory(File directory, Pattern pattern) throws IOException {
        ArrayList<String> found = new ArrayList<>();
        File[] fileList = directory.listFiles();

        if (fileList != null) {
            for (File file : fileList) {
                if (file.isDirectory()) {
                    found.addAll(getResourcesFromDirectory(file, pattern));
                } else {
                    final String fileName = file.getCanonicalPath();
                    final boolean accept = pattern.matcher(fileName).matches();
                    if (accept) {
                        found.add(fileName);
                    }
                }
            }
        }

        return found;
    }
}