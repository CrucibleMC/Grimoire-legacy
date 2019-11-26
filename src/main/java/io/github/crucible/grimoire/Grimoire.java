package io.github.crucible.grimoire;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

import java.util.Map;
import java.util.logging.Logger;

@IFMLLoadingPlugin.Name("Grimoire")
@IFMLLoadingPlugin.MCVersion("1.7.10")
public class Grimoire implements IFMLLoadingPlugin {

    private static Logger logger = Logger.getLogger("Grimoire");
    private static Grimoire instance;

    public Grimoire(){
        instance = this;

    }

    public static Grimoire getInstance() {
        return instance;
    }

    public static Logger getLogger() {
        return logger;
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
