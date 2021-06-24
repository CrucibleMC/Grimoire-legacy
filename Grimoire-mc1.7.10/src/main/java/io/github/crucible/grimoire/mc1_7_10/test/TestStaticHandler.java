package io.github.crucible.grimoire.mc1_7_10.test;

import io.github.crucible.grimoire.common.api.GrimoireAPI;
import io.github.crucible.grimoire.common.api.GrimoireConstants;
import io.github.crucible.grimoire.common.api.eventbus.CoreEventHandler;
import io.github.crucible.grimoire.common.api.eventbus.SubscribeCoreEvent;
import io.github.crucible.grimoire.common.api.events.grimmix.GrimmixFinishLoadEvent;

@CoreEventHandler(GrimoireConstants.MAIN_BUS_NAME)
public class TestStaticHandler {

    @SubscribeCoreEvent
    public static void onFinalization(GrimmixFinishLoadEvent event) {
        System.out.println("Received finish load event for: " + event.getEventOwner().getName());
    }

}
