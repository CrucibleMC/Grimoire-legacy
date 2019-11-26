package io.github.crucible.blackmagic;

import io.github.crucible.blackmagic.core.FileLoader;
import io.github.crucible.blackmagic.core.Utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class BlackMagic {

    public static boolean logInjections = true;

    public static void inject(ClassLoader targetClassLoader, FileLoader fileLoader) {

        Method addURL = null;
        
        try {
            addURL = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            addURL.setAccessible(true);
        } catch (NoSuchMethodException e) {
            System.out.println("[BlackMagic] the spell failed: " + e.getLocalizedMessage());
        }
        
        for(URL url : fileLoader.getFilesURL()){
            if(addURL != null && targetClassLoader != null){
                try{
                    addURL.invoke(targetClassLoader, url);

                    if(logInjections) System.out.println("[BlackMagic] The spell was casted successfully: " + Utils.getFileNameWithFormat(url.getFile()));

                } catch (IllegalAccessException | InvocationTargetException e) {
                    System.out.println("[BlackMagic] the spell failed: " + e.getLocalizedMessage());
                }
            }
        }
    }

    public static void inject(Class targetClassLoader, FileLoader fileLoader) {
        inject(targetClassLoader.getClassLoader(), fileLoader);
    }
}
