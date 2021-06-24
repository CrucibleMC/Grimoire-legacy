package io.github.crucible.grimoire.common.api.lib;

import java.util.function.Supplier;

import io.github.crucible.grimoire.common.GrimoireCore;

public enum Environment {

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
    public boolean isServer() {
        return !this.isClient();
    }

    /**
     * @return if this is the Client environment
     */
    public boolean isClient() {
        return this == CLIENT;
    }

    /**
     * Execute some arbitrary code if we are in this specific environment.
     * Double lamda wrapping to avoid classloading the target.
     * @param code
     */

    public void execute(Supplier<Runnable> code) {
        if (GrimoireCore.INSTANCE.getEnvironment() == this) {
            code.get().run();
        }
    }
}
