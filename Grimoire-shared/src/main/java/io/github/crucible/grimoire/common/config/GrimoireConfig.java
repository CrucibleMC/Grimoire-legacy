package io.github.crucible.grimoire.common.config;

import io.github.crucible.omniconfig.api.annotation.AnnotationConfig;
import io.github.crucible.omniconfig.api.annotation.ConfigLoadCallback;
import io.github.crucible.omniconfig.api.annotation.ConfigLoadCallback.Stage;
import io.github.crucible.omniconfig.api.annotation.properties.ConfigBoolean;
import io.github.crucible.omniconfig.api.builders.IOmniconfigBuilder;
import io.github.crucible.omniconfig.api.core.VersioningPolicy;

@AnnotationConfig(name = "GrimoireAPI", version = "1.0.0", reloadable = false, policy = VersioningPolicy.RESPECTFUL)
public class GrimoireConfig {

    @ConfigBoolean(comment = "Enable suppport for legacy patches. Does not include embedding support."
            + " Old patch system was heavily based on making assumptions about how mixin configuration .json's"
            + " should be named, so it has potential for conflicts with new grimmix system. Only use this if"
            + " you absolutely need it, and don't grow attached to this option being in place.")
    public static boolean enableLegacySupport = false;

}
