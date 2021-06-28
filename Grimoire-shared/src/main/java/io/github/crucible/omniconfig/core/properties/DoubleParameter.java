package io.github.crucible.omniconfig.core.properties;

import java.util.function.Function;

import io.github.crucible.omniconfig.api.OmniconfigConstants;
import io.github.crucible.omniconfig.api.builders.IDoublePropertyBuilder;
import io.github.crucible.omniconfig.api.properties.IDoubleProperty;
import io.github.crucible.omniconfig.backing.Configuration;
import io.github.crucible.omniconfig.core.Omniconfig;

public class DoubleParameter extends AbstractParameter<IDoubleProperty> implements IDoubleProperty {
    protected final double defaultValue, minValue, maxValue;
    protected final Function<Double, Double> validator;
    protected double value;

    public DoubleParameter(Builder builder) {
        super(builder);

        this.defaultValue = builder.defaultValue;
        this.minValue = builder.minValue;
        this.maxValue = builder.maxValue;
        this.validator = builder.validator;

        this.finishConstruction(builder);
    }

    @Override
    public double getValue() {
        this.assertValidEnvironment();
        return this.value;
    }

    @Override
    public double getMax() {
        this.assertValidEnvironment();
        return this.maxValue;
    }

    @Override
    public double getMin() {
        this.assertValidEnvironment();
        return this.minValue;
    }

    @Override
    public double getDefault() {
        this.assertValidEnvironment();
        return this.defaultValue;
    }

    @Override
    protected void load(Configuration config) {
        config.pushSynchronized(this.isSynchronized());
        if (this.validator != null) {
            config.pushValidator(this.validator);
        }
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

    @Override
    protected boolean valueMatchesDefault(Configuration inConfig) {
        this.load(inConfig);
        return this.value == this.defaultValue;
    }

    @Override
    protected boolean valuesMatchIn(Configuration one, Configuration two) {
        this.load(one);
        double valueOne = this.value;
        this.load(two);

        return valueOne == this.value;
    }

    public static Builder builder(Omniconfig.Builder parent, String name, double defaultValue) {
        return new Builder(parent, name, defaultValue);
    }

    public static class Builder extends AbstractParameter.Builder<IDoubleProperty, Builder> implements IDoublePropertyBuilder {
        protected final double defaultValue;
        protected double minValue = 0, maxValue = OmniconfigConstants.STANDART_INTEGER_LIMIT;
        protected Function<Double, Double> validator;

        protected Builder(Omniconfig.Builder parentBuilder, String name, double defaultValue) {
            super(parentBuilder, name);
            this.defaultValue = defaultValue;
        }

        @Override
        public Builder max(double maxValue) {
            this.maxValue = maxValue;
            return this;
        }

        @Override
        public Builder min(double minValue) {
            this.minValue = minValue;
            return this;
        }

        @Override
        public Builder minMax(double minMax) {
            this.min(-minMax);
            this.max(minMax);
            return this;
        }

        @Override
        public Builder validator(Function<Double, Double> validator) {
            this.validator = validator;
            return this;
        }

        @Override
        public DoubleParameter build() {
            this.finishBuilding();
            return new DoubleParameter(this);
        }

    }

}