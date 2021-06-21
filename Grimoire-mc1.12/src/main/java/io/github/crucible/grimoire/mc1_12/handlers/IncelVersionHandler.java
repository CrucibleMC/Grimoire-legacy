package io.github.crucible.grimoire.mc1_12.handlers;

import io.github.crucible.grimoire.common.core.VersionHandler;
import net.minecraftforge.fml.common.Loader;

public class IncelVersionHandler extends VersionHandler {

    @Override
    public boolean isModLoaded(String modid) {
        return Loader.isModLoaded(modid);
    }

    public static void init() {
        if (VersionHandler.instance() == null) {
            VersionHandler.setVersionHandler(new IncelVersionHandler());
        }
    }

}
