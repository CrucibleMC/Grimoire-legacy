package io.github.crucible.grimoire.mc1_7_10.test;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import io.github.crucible.grimoire.common.api.eventbus.SubscribeCoreEvent;
import io.github.crucible.grimoire.common.api.events.grimmix.GrimmixConfigBuildingEvent;
import io.github.crucible.grimoire.common.api.events.grimmix.GrimmixValidationEvent;

public class TestEventHandler {

    @SubscribeEvent
    public void validationEvent(GrimmixValidationEvent event) {
        System.out.println("Received validation event for grimmix: " + event.getEventOwner().getName());
    }

    @SubscribeCoreEvent
    public void configEvent(GrimmixConfigBuildingEvent event) {
        System.out.println("Received config event for grimmix: " + event.getEventOwner().getName());
    }

}
