package io.github.crucible.omniconfig.wrappers.values;

import io.github.crucible.omniconfig.OmniconfigCore;
import io.github.crucible.omniconfig.core.Configuration;
import io.github.crucible.omniconfig.wrappers.Omniconfig;
import io.github.crucible.omniconfig.wrappers.values.DoubleParameter.Builder;

public class DoubleParameter extends AbstractParameter<DoubleParameter> {
    protected final double defaultValue, minValue, maxValue;
    protected double value;

    public DoubleParameter(Builder builder) {
        super(builder);

        this.defaultValue = builder.defaultValue;
        this.minValue = builder.minValue;
        this.maxValue = builder.maxValue;

        this.finishConstruction(builder);
    }

    public double getValue() {
        return this.value;
    }

    public double getMax() {
        return this.maxValue;
    }

    public double getMin() {
        return this.minValue;
    }

    public double getDefault() {
        return this.defaultValue;
    }

    @Override
    protected void load(Configuration config) {
        config.pushSynchronized(this.isSynchronized());
        this.value = config.getDouble(this.name, this.category, this.defaultValue, this.minValue, this.maxValue, this.comment);
    }

    @Override
    public String valueToString() {
        return Double.toString(this.value);
    }

    @Override
    public void parseFromString(String value) {
        try {
            double parsed = Double.parseDouble(value);
            this.value = parsed < this.minValue ? this.minValue : (parsed > this.maxValue ? this.maxValue : parsed);
        } catch (Exception e) {
            this.logGenericParserError(value);
        }
    }

    @Override
    public String toString() {
        return this.valueToString();
    }

    public static Builder builder(Omniconfig.Builder parent, String name, double defaultValue) {
        return new Builder(parent, name, defaultValue);
    }

    public static class Builder extends AbstractParameter.Builder<DoubleParameter, Builder> {
        protected final double defaultValue;
        protected double minValue = 0, maxValue = OmniconfigCore.STANDART_INTEGER_LIMIT;

        protected Builder(Omniconfig.Builder parentBuilder, String name, double defaultValue) {
            super(parentBuilder, name);
            this.defaultValue = defaultValue;
        }

        public Builder max(double maxValue) {
            this.maxValue = maxValue;
            return this;
        }

        public Builder min(double minValue) {
            this.minValue = minValue;
            return this;
        }

        public Builder minMax(double minMax) {
            this.min(-minMax);
            this.max(minMax);
            return this;
        }

        @Override
        public DoubleParameter build() {
            this.finishBuilding();
            return new DoubleParameter(this);
        }
    }

}