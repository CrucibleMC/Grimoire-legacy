package io.github.crucible.omniconfig.api;

import io.github.crucible.omniconfig.backing.Configuration;

public class OmniconfigConstants {

    /**
     * Category splitter char. May be used when specifying config subcategories
     * with annotation config.
     */
    public static final String CATEGORY_SPLITTER = Configuration.CATEGORY_SPLITTER;

    /**
     * Sort of "default" category config files may use for general stuff.
     */
    public static final String GENERAL_CATEGORY = Configuration.CATEGORY_GENERAL;

}
