package io.github.crucible.grimoire.common.api.eventbus;

/**
 * Base event class for {@link CoreEventBus}. Any events dispatched by
 * that particular implementation of event bus must have this one
 * in their superclass hierarchy.
 *
 * @author Aizistral
 */

public abstract class CoreEvent {
    private boolean isCanceled = false;
    private Result result = Result.DEFAULT;

    /**
     * @return True if this event can be canceled, false otherwise.
     */
    public boolean isCancelable() {
        return this instanceof ICancelable;
    }

    /**
     * @return True if this event is already canceled and should
     * not propagate to event receivers that are not explicitly marked
     * as such that receive canceled events; false if not.<br>
     * If event is not cancelable, this will always return false.
     */
    public boolean isCanceled() {
        return this.isCanceled;
    }

    /**
     * Sets the cancel state of this event. Note, not all events are cancelable,
     * and any attempt to invoke this method on an event that is not cancelable
     * (as determined by {@link #isCancelable} will result in an
     * {@link UnsupportedOperationException}.<br>
     * The functionality of setting the canceled state is defined on a per-event
     * basis.
     *
     * @param cancel The new canceled state.
     * @throws UnsupportedOperationException in case event is not cancelable.
     */
    public void setCanceled(boolean cancel) throws UnsupportedOperationException {
        if (!this.isCancelable())
            throw new UnsupportedOperationException(
                    "Attempted to call CoreEvent#setCanceled() on a non-cancelable event of type: "
                            + this.getClass().getCanonicalName());

        this.isCanceled = cancel;
    }

    /**
     * @return True if this event has {@link Result}, false otherwise.
     */
    public boolean hasResult() {
        return this instanceof IHasResult;
    }

    /**
     * @return The {@link Result} of this event, if it {@link #hasResult()}.<br>
     * If event does not have a result, {@link Result#DEFAULT} will always
     * be returned.
     */
    public Result getResult() {
        return this.result;
    }

    /**
     * Sets new {@link Result} for this event. Be aware that attempting to set
     * result on event that does not have result, as determined by {@link #hasResult()},
     * will result in an {@link UnsupportedOperationException}.
     *
     * @param result New {@link Result} this event should have.
     * @throws UnsupportedOperationException in case event does not have result.
     */
    public void setResult(Result result) throws UnsupportedOperationException {
        if (!this.hasResult())
            throw new UnsupportedOperationException(
                    "Attempted to call CoreEvent#setCanceled() on a result-less event of type: "
                            + this.getClass().getCanonicalName());

        this.result = result;
    }


    /**
     * Priority allows individual event receivers to change order in which they
     * will receive an event instance, compared to other such receivers.<br>
     * When dispatching any event, valid receivers for that event are sorted from
     * highest to lowers priority, and only then are iterated over and invoked.
     *
     * @author Aizistral
     */
    public enum Priority {
        HIGHEST, // First to execute
        HIGH,
        NORMAL,
        LOW,
        LOWEST // Last to execute
    }

    /**
     * Generic "result", may come in handy for certain events.<br>
     * It is normally used for events that are associated with some sort of
     * "conditional logic". And example of that from Forge events could be
     * mob spawn event - it is fired for any attempt to spawn a mod somewhere,
     * but not every attempt will result in mob actually spawning by default.
     * Setting result to <code>DENY</code> on such event supposedly prevents
     * mob from spawning at all times, and setting it to <code>ALLOW</code>
     * would force mob to spawn regardless of whether or not spawn logic has
     * all internal conditions satisfied.
     *
     * @author Aizistral
     */
    public enum Result {

        /**
         * Forbid or brevent some sort of behavior associated with this event,
         * supposedly to override it with some other logic, or just not let
         * it happen.
         */
        DENY,

        /**
         * Pretend event was not touched upon by external handlers; let original
         * logic associated with this event execute as normal.
         */
        DEFAULT,

        /**
         * Force some sort of behavior associated with this event to happen,
         * regardless of whether or not it satisfies its "normal" conditions.
         */
        ALLOW
    }
}
