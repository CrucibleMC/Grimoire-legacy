package io.github.crucible.grimoire.mc1_7_10.test;

import io.github.crucible.grimoire.common.api.GrimoireAPI;
import io.github.crucible.grimoire.common.api.GrimoireConstants;
import io.github.crucible.grimoire.common.api.eventbus.CoreEventHandler;
import io.github.crucible.grimoire.common.api.eventbus.SubscribeCoreEvent;
import io.github.crucible.grimoire.common.api.events.grimmix.GrimmixFinishLoadEvent;
import io.github.crucible.grimoire.common.api.events.grimmix.GrimmixModLoadEvent;

@CoreEventHandler(GrimoireConstants.MAIN_BUS_NAME)
public class TestStaticHandler {

    @SubscribeCoreEvent
    public static void onFinalization(GrimmixModLoadEvent event) {
        System.out.println("Received mod load event for: " + event.getEventOwner().getName());
    }

}
