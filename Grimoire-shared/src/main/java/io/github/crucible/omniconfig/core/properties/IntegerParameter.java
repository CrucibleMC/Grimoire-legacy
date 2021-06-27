package io.github.crucible.omniconfig.core.properties;

import java.util.function.Function;

import io.github.crucible.omniconfig.OmniconfigCore;
import io.github.crucible.omniconfig.api.builders.IIntegerPropertyBuilder;
import io.github.crucible.omniconfig.api.properties.IIntegerProperty;
import io.github.crucible.omniconfig.backing.Configuration;
import io.github.crucible.omniconfig.core.Omniconfig;

public class IntegerParameter extends AbstractParameter<IIntegerProperty> implements IIntegerProperty {
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

    @Override
    public int getValue() {
        this.assertValidEnvironment();
        return this.value;
    }

    @Override
    public int getMax() {
        this.assertValidEnvironment();
        return this.maxValue;
    }

    @Override
    public int getMin() {
        this.assertValidEnvironment();
        return this.minValue;
    }

    @Override
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

    @Override
    protected boolean valueMatchesDefault(Configuration inConfig) {
        this.load(inConfig);
        return this.value == this.defaultValue;
    }

    @Override
    protected boolean valuesMatchIn(Configuration one, Configuration two) {
        this.load(one);
        int valueOne = this.value;
        this.load(two);

        return valueOne == this.value;
    }


    public static Builder builder(Omniconfig.Builder parent, String name, int defaultValue) {
        return new Builder(parent, name, defaultValue);
    }

    public static class Builder extends AbstractParameter.Builder<IIntegerProperty, Builder> implements IIntegerPropertyBuilder {
        protected final int defaultValue;
        protected int minValue = 0, maxValue = OmniconfigCore.STANDART_INTEGER_LIMIT;
        protected Function<Integer, Integer> validator;

        protected Builder(Omniconfig.Builder parentBuilder, String name, int defaultValue) {
            super(parentBuilder, name);
            this.defaultValue = defaultValue;
        }

        @Override
        public Builder max(int maxValue) {
            this.maxValue = maxValue;
            return this;
        }

        @Override
        public Builder min(int minValue) {
            this.minValue = minValue;
            return this;
        }

        @Override
        public Builder minMax(int minMax) {
            this.min(-minMax);
            this.max(minMax);
            return this;
        }

        @Override
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