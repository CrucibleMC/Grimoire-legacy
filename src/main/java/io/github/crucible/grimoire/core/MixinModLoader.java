package io.github.crucible.grimoire.core;


import io.github.crucible.blackmagic.BlackMagic;
import io.github.crucible.blackmagic.core.FileLoader;
import io.github.crucible.grimoire.Grimoire;
import org.spongepowered.asm.mixin.Mixins;


import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class MixinModLoader {

    public void init(){
        File dir = new File("mixinmods");

        if(!dir.exists()){
            if(dir.mkdirs()){
                Grimoire.getLogger().info("[Grinmoire] Criando pasta para os mods.");
            }
        }

        File[] files;
        if((files = dir.listFiles()) != null){
            for(File mods : files){
                try {

                    if(mods.isDirectory()) continue;

                    ZipFile zipFile = new ZipFile(mods);

                    Enumeration<? extends ZipEntry> entries = zipFile.entries();
                    ZipEntry mixinjson = null;

                    while (entries.hasMoreElements()) {
                        ZipEntry entry = entries.nextElement();
                        if (entry.getName().endsWith("-mixin.json")) {
                            mixinjson = entry;
                            break;
                        }
                    }

                    if(mixinjson != null){
                        BlackMagic.inject(this.getClass(), new FileLoader(mods.getCanonicalPath()));
                        Mixins.addConfiguration(mixinjson.getName());
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
