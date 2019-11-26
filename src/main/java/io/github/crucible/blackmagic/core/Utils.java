package io.github.crucible.blackmagic.core;


import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Utils {

    public static File toFile(InputStream in, String name, String suffix) throws IOException {
        final File tempFile = File.createTempFile(name, suffix);
        tempFile.deleteOnExit();
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            IOUtils.copy(in, out);
        }
        return tempFile;
    }

    public static String getEnds(String path){

        if(path.toLowerCase().endsWith(".jar")){
            return ".jar";
        }

        if(path.toLowerCase().endsWith(".zip")){
            return ".zip";
        }

        return "";
    }

    private static String lastIndex(String[] array){
        return (array.length > 1) ? array[(array.length - 1)] : (array[0] != null) ? array[0] : "";
    }

    public static String getFileName(String stream){
        if(!(getEnds(stream)).equals("")){

            //Sanity check
            try{
                return lastIndex(stream.substring(0,(stream.length() - 4)).split("/"));
            }catch (Exception e){
                e.printStackTrace();
                return "";
            }

        }
        return "";
    }

    public static String getFileNameWithFormat(String stream){
        return getFileName(stream)+getEnds(stream);
    }

    public static String getTempName(String stream){

        if(!(getEnds(stream)).equals("")){

            //Sanity check
            try{
                return lastIndex(stream.substring(0,(stream.length() - 4) - 19).split("/"));
            }catch (Exception e){
                return "";
            }

        }
        return "";
    }

    public static String getTempNameWithFormat(String stream){
        return getTempName(stream)+getEnds(stream);
    }

}
