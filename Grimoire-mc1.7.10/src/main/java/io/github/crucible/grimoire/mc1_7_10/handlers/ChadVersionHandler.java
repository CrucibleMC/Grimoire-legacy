package io.github.crucible.grimoire.mc1_7_10.handlers;

import cpw.mods.fml.common.Loader;
import io.github.crucible.grimoire.common.core.VersionHandler;

public class ChadVersionHandler extends VersionHandler {

    @Override
    public boolean isModLoaded(String modid) {
        return Loader.isModLoaded(modid);
    }

    public static void init() {
        if (VersionHandler.instance() == null) {
            VersionHandler.setVersionHandler(new ChadVersionHandler());
        }
    }

}
