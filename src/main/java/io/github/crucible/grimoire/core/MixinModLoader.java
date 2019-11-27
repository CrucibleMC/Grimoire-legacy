package io.github.crucible.grimoire.core;

import io.github.crucible.blackmagic.BlackMagic;
import io.github.crucible.blackmagic.core.FileLoader;
import io.github.crucible.grimoire.Grimoire;
import org.spongepowered.asm.mixin.Mixins;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class MixinModLoader {

    private static ArrayList<File> mixin_mods = new ArrayList<>();

    public static void init(){
        File dir = new File("grimoire");

        if(!dir.exists()){
            if(dir.mkdirs()){
                Grimoire.getLogger().info("[Grimoire] Criando pasta para os mods.");
            }
        }

        File[] files;
        if((files = dir.listFiles()) != null){
            for(File mod : files){
                try {

                    if(mod.isDirectory()) continue;

                    ZipFile zipFile = new ZipFile(mod);

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
                        try{

                            BlackMagic.inject(MixinModLoader.class, new FileLoader(mod.getCanonicalPath()));
                            Mixins.addConfiguration(mixinjson.getName());

                            mixin_mods.add(mod);

                        }catch (Exception e){
                            Grimoire.getLogger().info("[Grimoire] Falha ao carregar \'" + mod.getName() + "\' arquivo inválido." );
                        }
                    }


                } catch (IOException e) {
                    Grimoire.getLogger().info("[Grimoire] Falha ao carregar \'" + mod.getName() + "\' arquivo inválido." );
                }
            }
        }
    }

    public static ArrayList<File> getLoadedMixins() {
        return mixin_mods;
    }
}
