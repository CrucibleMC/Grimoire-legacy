package io.github.crucible.grimoire.common.api.events;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class SimpleEventHandler {
    private final List<IListener> listeners = new ArrayList<>();

    public boolean post(GrimoireEvent event) {
        if (event instanceof ICancelable) {
            ICancelable cancellableEvent = (ICancelable) event;
            for (IListener listener : listeners) {
                if (!cancellableEvent.isCanceled() || listener.handleCancelled())
                    listener.handle(event);
            }
            return cancellableEvent.isCanceled();
        } else {
            for (IListener listener : listeners) {
                listener.handle(event);
            }
            return false;
        }
    }

    public void register(IListener... toRegister) {
        listeners.addAll(Arrays.asList(toRegister));
        listeners.sort(Comparator.comparingInt(IListener::priority));
    }

    public void unregister(IListener... toRemove) {
        listeners.removeAll(Arrays.asList(toRemove));
    }
}
