package io.github.crucible.grimoire.common.proxy;

import io.github.crucible.grimoire.common.api.lib.Environment;

public class ClientProxy extends CommonProxy {

    public ClientProxy() {
        super();

        this.side = Environment.CLIENT;
    }



}
