package io.github.crucible.grimoire.common.proxy;

import io.github.crucible.grimoire.common.api.lib.Environment;

public class CommonProxy {
    protected Environment side;

    public CommonProxy() {
        this.side = Environment.DEDICATED_SERVER;
    }


}
