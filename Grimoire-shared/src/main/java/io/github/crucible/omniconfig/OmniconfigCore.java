package io.github.crucible.omniconfig;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.github.crucible.grimoire.common.core.GrimoireCore;

public class OmniconfigCore {

    public static final Logger logger = LogManager.getLogger("OmniConfig");
    public static final File CONFIG_DIR = new File(GrimoireCore.INSTANCE.getMCLocation(), "config");
    public static final int STANDART_INTEGER_LIMIT = 32768;

}
