package io.github.crucible.grimoire.mc1_7_10.integrations.eventhelper;

import cpw.mods.fml.common.Loader;
import io.github.crucible.grimoire.common.integrations.IIntegration;

public class EventHelperIntegration implements IIntegration<IEventHelperIntegration> {
    private IEventHelperIntegration myIntegration;

    @Override
    public void initIntegration() {
        if (Loader.isModLoaded("EventHelper")) {
            this.myIntegration = new RealEventHelperIntegration();
        } else {
            this.myIntegration = new DummyEventHelperIntegration();
        }
    }

    @Override
    public IEventHelperIntegration getIntegration() {
        return this.myIntegration;
    }
}
