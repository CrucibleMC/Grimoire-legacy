package io.github.crucible.grimoire.common.api.lib;

public enum Side {

    /**
     * The client side. Specifically, an environment where rendering capability exists.
     * Usually in the game client.
     */
    CLIENT,
    /**
     * The server side. Specifically, an environment where NO rendering capability exists.
     * Usually on the dedicated server.
     */
    DEDICATED_SERVER;

    /**
     * @return If this is the server environment
     */
    public boolean isServer()
    {
        return !this.isClient();
    }

    /**
     * @return if this is the Client environment
     */
    public boolean isClient()
    {
        return this == CLIENT;
    }
}
