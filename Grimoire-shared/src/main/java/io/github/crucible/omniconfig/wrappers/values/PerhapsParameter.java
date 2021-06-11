package io.github.crucible.omniconfig.wrappers.values;

import io.github.crucible.omniconfig.OmniconfigCore;
import io.github.crucible.omniconfig.core.Configuration;
import io.github.crucible.omniconfig.lib.Perhaps;
import io.github.crucible.omniconfig.wrappers.Omniconfig;

public class PerhapsParameter extends AbstractParameter<PerhapsParameter> {
    protected final Perhaps defaultValue;
    protected final double minValue, maxValue;
    protected Perhaps value;

    public PerhapsParameter(Builder builder) {
        super(builder);
        this.defaultValue = builder.defaultValue;
        this.minValue = builder.minValue;
        this.maxValue = builder.maxValue;

        this.finishConstruction(builder);
    }

    public Perhaps getDefault() {
        return this.defaultValue;
    }

    public Perhaps getValue() {
        return this.value;
    }

    public double getMin() {
        return this.minValue;
    }

    public double getMax() {
        return this.maxValue;
    }

    @Override
    protected void load(Configuration config) {
        config.pushSynchronized(this.isSynchronized);
        this.value = Perhaps.fromPercent(config.getDouble(this.name, this.category, this.defaultValue.asPercent(), this.minValue, this.maxValue, this.comment));
    }

    @Override
    public String valueToString() {
        return Double.toString(this.value.asPercent());
    }

    @Override
    public void parseFromString(String value) {
        try {
            double parsed = Double.parseDouble(value);
            double percentage = parsed < this.minValue ? this.minValue : (parsed > this.maxValue ? this.maxValue : parsed);

            this.value = Perhaps.fromPercent(percentage);
        } catch (Exception e) {
            this.logGenericParserError(value);
        }
    }

    @Override
    public String toString() {
        return this.valueToString();
    }

    public static Builder builder(Omniconfig.Builder parentBuilder, String name, Perhaps defaultValue) {
        return new Builder(parentBuilder, name, defaultValue);
    }

    public static class Builder extends AbstractParameter.Builder<PerhapsParameter, Builder> {
        protected final Perhaps defaultValue;
        protected double minValue = 0, maxValue = 100;

        protected Builder(Omniconfig.Builder parentBuilder, String name, Perhaps defaultValue) {
            super(parentBuilder, name);
            this.defaultValue = defaultValue;
        }

        public Builder max(double percent) {
            this.maxValue = percent;
            return this;
        }

        public Builder min(double percent) {
            this.minValue = percent;
            return this;
        }

        public Builder minMax(double percent) {
            this.min(-percent);
            this.max(percent);
            return this;
        }

        @Override
        public PerhapsParameter build() {
            this.finishBuilding();
            return new PerhapsParameter(this);
        }

    }

}