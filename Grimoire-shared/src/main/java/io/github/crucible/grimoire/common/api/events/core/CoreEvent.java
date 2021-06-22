package io.github.crucible.grimoire.common.api.events.core;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Base Event class that all other events are derived from
 */
public abstract class CoreEvent {
    private boolean isCanceled = false;

    /**
     * Determine if this function is cancelable at all.
     *
     * @return If access to setCanceled should be allowed
     * <p>
     */
    public boolean isCancelable() {
        return this instanceof ICancelable;
    }

    /**
     * Determine if this event is canceled and should stop executing.
     *
     * @return The current canceled state
     */
    public boolean isCanceled() {
        return this.isCanceled;
    }

    /**
     * Sets the cancel state of this event. Note, not all events are cancelable, and any attempt to
     * invoke this method on an event that is not cancelable (as determined by {@link #isCancelable}
     * will result in an {@link UnsupportedOperationException}.
     * <p>
     * The functionality of setting the canceled state is defined on a per-event bases.
     *
     * @param cancel The new canceled value
     */
    public void setCanceled(boolean cancel) {
        if (!this.isCancelable())
            throw new UnsupportedOperationException(
                    "Attempted to call Event#setCanceled() on a non-cancelable event of type: "
                            + this.getClass().getCanonicalName());

        this.isCanceled = cancel;
    }

}
