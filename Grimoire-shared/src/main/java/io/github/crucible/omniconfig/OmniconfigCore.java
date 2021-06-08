package io.github.crucible.omniconfig;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.github.crucible.grimoire.common.core.GrimoireCore;

public class OmniconfigCore {

    public static final Logger logger = LogManager.getLogger("Omniconfig");
    public static final File CONFIG_DIR = new File(GrimoireCore.INSTANCE.getMCLocation(), "config");
    public static final int STANDART_INTEGER_LIMIT = 32768;

    /**
     * This must only ever true if we are in a client environment and
     * currently are logged in to non-local server.
     */
    public static boolean onRemoteServer = false;

}
