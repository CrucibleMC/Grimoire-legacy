package io.github.crucible.omniconfig.api.lib;

import java.util.Random;

/**
 * A percentage-based value that can be converted into factor,
 * or 1.0 + factor in case needed.<br><br>
 *
 * For the reference, consider that <b><code>factor</code> =
 * <code>percentage/100.0</code></b>, and <b><code>percentage</code> =
 * <code>factor*100.0</code></b>.
 *
 * @author Aizistral
 */

public class Perhaps {
    public static final Random theySeeMeRollin = new Random();
    private final double value;

    /**
     * Create new {@link Perhaps} from factor.
     * @param value Factor value.
     * @return New {@link Perhaps} based on passed factor value.
     */
    public static Perhaps fromValue(double value) {
        return new Perhaps(value);
    }

    /**
     * Create new {@link Perhaps} from percentage.
     * @param percentage Percentage value.
     * @return New {@link Perhaps} based on passed percentage value.
     */
    public static Perhaps fromPercent(double percentage) {
        return new Perhaps(percentage / 100.0);
    }

    private Perhaps(double value) {
        this.value = value;
    }

    /**
     * Tries to generate random double in range from 0.0 (inclusive) to 1.0 (exclusive).
     * @return If factor-based value of this {@link Perhaps} is greater or equal to generated
     * double, returns true; otherwise returns false.
     */
    public boolean roll() {
        return theySeeMeRollin.nextDouble() <= this.value;
    }

    /**
     * @return Percentage-based value of this {@link Perhaps}.
     */
    public double asPercent() {
        return this.value * 100.0;
    }

    /**
     * @return Factor-based value of this {@link Perhaps}.
     */
    public double asMultiplier() {
        return this.value;
    }

    /**
     * @return Factor-based value of this {@link Perhaps} + 1.0.
     */
    public double asBasedMultiplier() {
        return 1.0 + this.value;
    }

    /**
     * @return 1.0 - factor-based value of this {@link Perhaps}.
     */
    public double asInvertedMultiplier() {
        return 1.0 - this.value;
    }

    /**
     * @return Factor-based value of this {@link Perhaps}, in float form.
     */
    public float asFloatplier() {
        return (float) this.asMultiplier();
    }

    /**
     * @return Factor-based value of this {@link Perhaps} + 1.0, in float form.
     */
    public float asBasedFloatplier() {
        return (float) this.asBasedMultiplier();
    }

    /**
     * @return 1.0 - factor-based value of this {@link Perhaps}, in float form.
     */
    public float asInvertedFloatplier() {
        return (float) this.asInvertedMultiplier();
    }

    /**
     * Makes string out of this {@link Perhaps}.
     *
     * @return Percentage-based string representation of this {@link Perhaps}.
     * For instance, {@link Perhaps} with factor-based value of <code>0.253</code>
     * will return a {@link String} that looks like this: <code>"25.3%"</code>
     */
    @Override
    public String toString() {
        double percent = this.asPercent();

        if (Math.floor(percent) == percent)
            return Integer.toString((int) percent) + "%";
        else
            return Double.toString(percent) + "%";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Perhaps) {
            Perhaps another = (Perhaps) obj;
            return this.value == another.value;
        }

        return false;
    }

}
