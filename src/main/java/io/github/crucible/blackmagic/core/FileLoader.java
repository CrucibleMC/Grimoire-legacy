package io.github.crucible.blackmagic.core;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;


public class FileLoader {

    private List<File> fileList = new ArrayList<>();

    public FileLoader(Class resourceClass, String directory){
        this(resourceClass,directory,false);
    }

    public FileLoader(Class resourceClass, String directory, boolean isZipIncluded){
        if(directory == null) throw new NullPointerException();

        InputStream stream;
        List<String> filesString = Collections.emptyList();

        if((stream = resourceClass.getClassLoader().getResourceAsStream(directory)) != null){
            try {
                filesString = IOUtils.readLines(stream, Charset.forName("UTF-8"));
            }catch (IOException e) {
                e.printStackTrace();
            }
        }

        for(String path : filesString){

            if(path == null) return;

            String fDirectory = "";
            if(directory.endsWith("/")){
                if(!directory.startsWith("/")) fDirectory = "/"+directory;
            }else {
                if (!directory.startsWith("/")){
                    fDirectory = "/"+directory+"/";
                }else {
                    fDirectory = directory+"/";
                }
            }

            stream = resourceClass.getResourceAsStream(fDirectory+path);

            if(stream != null){
                File file = null;
                try{

                    if((Utils.getEnds(path).equalsIgnoreCase(".zip") && !isZipIncluded) || Utils.getEnds(path).equalsIgnoreCase("")) return;
                    file = Utils.toFile(stream,Utils.getFileName(path),Utils.getEnds(path));

                } catch (IOException ex) {
                    System.out.println("[FileLoader] Something are wrong: " + ex.getLocalizedMessage());
                }

                if(file != null){
                    fileList.add(file);
                }
            }
        }

    }

    public FileLoader(String path){
        this(path,false);
    }

    public FileLoader(String path, boolean isZipIncluded){


        File file;
        if((file = new File(path)).exists()){

            if(file.isDirectory()){
                fileList = scanFolder(file, isZipIncluded);
            }

            String name = file.getName().toLowerCase();

            if(name.endsWith(".jar") || (name.endsWith(".zip") && isZipIncluded)){
                fileList = Collections.singletonList(file);
            }
        }
    }

    private List<File> scanFolder(File path, boolean isZipIncluded){
        List<Path> files;
        List<File> fileList = new ArrayList<>();

        try{
            files = Files.walk(path.toPath()).filter(f -> !Files.isDirectory(f)).collect(Collectors.toList());
        } catch (IOException e) {
            return Collections.emptyList();
        }

        for(Path p : files) {
            File file;

            if ((file = p.toFile()).exists()) {

                if(file.canRead()){
                    String name = file.getName().toLowerCase();

                    if(name.endsWith(".jar") || (name.endsWith(".zip") && isZipIncluded)){
                        fileList.add(file);
                    }
                }
            }
        }

        return fileList;
    }

    public URL[] getFilesURL() {
        ArrayList<URL> urls = new ArrayList<>();

        for(File file : getFiles()){
            try{
                urls.add(file.toURI().toURL());
            }catch (MalformedURLException ignored){}
        }

        return urls.toArray(new URL[0]);
    }

    public List<File> getFiles() {
        return fileList;
    }

    public int getSize(){
        return fileList.size();
    }

}
