package io.github.crucible.omniconfig.wrappers.values;

import java.util.function.Consumer;
import java.util.function.Function;

import io.github.crucible.omniconfig.OmniconfigCore;
import io.github.crucible.omniconfig.core.Configuration;
import io.github.crucible.omniconfig.wrappers.Omniconfig;

public class IntegerParameter extends AbstractParameter<IntegerParameter> {
    protected final int defaultValue, minValue, maxValue;
    protected final Function<Integer, Integer> validator;
    protected int value;

    public IntegerParameter(Builder builder) {
        super(builder);

        this.defaultValue = builder.defaultValue;
        this.minValue = builder.minValue;
        this.maxValue = builder.maxValue;
        this.validator = builder.validator;

        this.finishConstruction(builder);
    }

    public int getValue() {
        this.assertValidEnvironment();
        return this.value;
    }

    public int getMax() {
        this.assertValidEnvironment();
        return this.maxValue;
    }

    public int getMin() {
        this.assertValidEnvironment();
        return this.minValue;
    }

    public int getDefault() {
        this.assertValidEnvironment();
        return this.defaultValue;
    }

    @Override
    protected void load(Configuration config) {
        config.pushSynchronized(this.isSynchronized());
        if (this.validator != null) {
            config.pushValidator(this.validator);
        }
        this.value = config.getInt(this.name, this.category, this.defaultValue, this.minValue, this.maxValue, this.comment);
    }

    @Override
    public String valueToString() {
        return Integer.toString(this.value);
    }

    @Override
    public void parseFromString(String value) {
        try {
            int parsed = Integer.parseInt(value);
            this.value = parsed < this.minValue ? this.minValue : (parsed > this.maxValue ? this.maxValue : parsed);
        } catch (Exception e) {
            this.logGenericParserError(value);
        }
    }

    @Override
    public String toString() {
        return this.valueToString();
    }

    public static Builder builder(Omniconfig.Builder parent, String name, int defaultValue) {
        return new Builder(parent, name, defaultValue);
    }

    public static class Builder extends AbstractParameter.Builder<IntegerParameter, Builder> {
        protected final int defaultValue;
        protected int minValue = 0, maxValue = OmniconfigCore.STANDART_INTEGER_LIMIT;
        protected Function<Integer, Integer> validator;

        protected Builder(Omniconfig.Builder parentBuilder, String name, int defaultValue) {
            super(parentBuilder, name);
            this.defaultValue = defaultValue;
        }

        public Builder max(int maxValue) {
            this.maxValue = maxValue;
            return this;
        }

        public Builder min(int minValue) {
            this.minValue = minValue;
            return this;
        }

        public Builder minMax(int minMax) {
            this.min(-minMax);
            this.max(minMax);
            return this;
        }

        public Builder validator(Function<Integer, Integer> validator) {
            this.validator = validator;
            return this;
        }

        @Override
        public IntegerParameter build() {
            this.finishBuilding();
            return new IntegerParameter(this);
        }
    }

}