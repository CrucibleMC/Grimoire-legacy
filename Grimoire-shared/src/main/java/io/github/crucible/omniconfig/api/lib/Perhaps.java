package io.github.crucible.omniconfig.api.lib;

import java.util.Random;

/**
 * A percentage-based value that can be converted into factor,
 * or 1.0 + factor in case it's required.
 * @author Integral
 */

public class Perhaps {
    public static final Random theySeeMeRollin = new Random();
    private final double value;

    public static Perhaps fromValue(double value) {
        return new Perhaps(value);
    }

    public static Perhaps fromPercent(double percentage) {
        return new Perhaps(percentage / 100.0);
    }

    private Perhaps(double value) {
        this.value = value;
    }

    public boolean roll() {
        return theySeeMeRollin.nextDouble() <= this.value;
    }

    public double asPercent() {
        return this.value * 100.0;
    }

    public double asMultiplier() {
        return this.value;
    }

    public double asBasedMultiplier() {
        return 1.0 + this.value;
    }

    public double asInvertedMultiplier() {
        return 1.0 - this.value;
    }

    public float asFloatplier() {
        return (float) this.asMultiplier();
    }

    public float asBasedFloatplier() {
        return (float) this.asBasedMultiplier();
    }

    public float asInvertedFloatplier() {
        return (float) this.asInvertedMultiplier();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Perhaps) {
            Perhaps another = (Perhaps) obj;
            return this.value == another.value;
        }

        return false;
    }

    @Override
    public String toString() {
        double percent = this.asPercent();

        if (Math.floor(percent) == percent)
            return Integer.toString((int) percent) + "%";
        else
            return Double.toString(percent) + "%";
    }

}
