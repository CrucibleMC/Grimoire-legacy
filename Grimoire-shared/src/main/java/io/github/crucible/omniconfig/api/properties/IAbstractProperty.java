package io.github.crucible.omniconfig.api.properties;

import io.github.crucible.omniconfig.api.core.IOmniconfig;

/**
 * Exposes methods that can be used to interact with any {@link IOmniconfig}
 * property; type-specific interfaces iherit from this one. The general contract
 * of all omniconfig properties is that once built, they are read-only. The only
 * way their value can change later in runtime is if config file was set as
 * reloadable - actual values contained by properties will automatically be
 * updated when config file itself is reloaded. However, all data that is exposed
 * by this particular interface will remain persistent at all times.
 *
 * @author Aizistral
 */

public abstract interface IAbstractProperty {

    /**
     * @return {@link String}-form ID used to distinguish this property among others.
     * It normally will look like <code>[config_category]$[property_name]</code>
     */
    public String getID();

    /**
     * @return Category name this property belongs to. If category is a part of
     * subcategory hierarchy, returned {@link String} will include names of all parent
     * categories in that hierarchy; singular category names are separated from one
     * another with <code>$</code> symbol.
     */
    public String getCategory();

    /**
     * @return Name of this property, as used in physical config file.
     */
    public String getName();

    /**
     * @return (Supposedly) human-readable comment describing this particular property.
     */
    public String getComment();

    /**
     * @return Whether server will attempt to synchronize value of this config property
     * to every remote client that logs into it. On client, synchronized properties keep
     * their values equal to those present on remote server, from the time of logging in
     * to the server and until logging out of it. When logging out, synchronized values
     * are discarded and automatically reloaded from local file.<br><br>
     *
     * Proberties <b>can</b> be synchronized even if their config file is not marked as
     * reloadable. Logging into and out of server will then be the only time their value
     * can change.
     */
    public boolean isSynchronized();

}
