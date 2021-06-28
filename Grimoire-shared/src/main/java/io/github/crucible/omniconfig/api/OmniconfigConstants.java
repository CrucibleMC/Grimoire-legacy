package io.github.crucible.omniconfig.api;

import io.github.crucible.omniconfig.backing.Configuration;

/**
 * Some things never change.
 *
 * @author Aizistral
 */

public class OmniconfigConstants {

    private OmniconfigConstants() {
        // Can't touch this
    }

    /**
     * Category splitter char. May be used when specifying config subcategories
     * with annotation config.
     */
    public static final String CATEGORY_SPLITTER = Configuration.CATEGORY_SPLITTER;

    /**
     * Sort of "default" category config files may use for general stuff.
     */
    public static final String GENERAL_CATEGORY = Configuration.CATEGORY_GENERAL;

    /**
     * Default comment which will be assigned to all config properties built without
     * specifying individual comment of their own.
     */

    public static final String DEFAULT_COMMENT = "Undocumented property.";
    /**
     * Serves as default upper bound for all numerical properties.
     */
    public static final int STANDART_INTEGER_LIMIT = 32768;

}
