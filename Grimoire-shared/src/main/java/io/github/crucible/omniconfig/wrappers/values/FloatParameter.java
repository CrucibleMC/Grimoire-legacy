package io.github.crucible.omniconfig.wrappers.values;

import java.util.function.Function;

import io.github.crucible.omniconfig.OmniconfigCore;
import io.github.crucible.omniconfig.core.Configuration;
import io.github.crucible.omniconfig.wrappers.Omniconfig;
import io.github.crucible.omniconfig.wrappers.values.FloatParameter.Builder;

public class FloatParameter extends AbstractParameter<FloatParameter> {
    protected final float defaultValue, minValue, maxValue;
    protected final Function<Float, Float> validator;
    protected float value;

    public FloatParameter(Builder builder) {
        super(builder);

        this.defaultValue = builder.defaultValue;
        this.minValue = builder.minValue;
        this.maxValue = builder.maxValue;
        this.validator = builder.validator;

        this.finishConstruction(builder);
    }

    public float getValue() {
        this.assertValidEnvironment();
        return this.value;
    }

    public float getMax() {
        this.assertValidEnvironment();
        return this.maxValue;
    }

    public float getMin() {
        this.assertValidEnvironment();
        return this.minValue;
    }

    public float getDefault() {
        this.assertValidEnvironment();
        return this.defaultValue;
    }

    protected Double validationWrapper(Double value) {
        float result = this.validator.apply(value.floatValue());
        return (double)result;
    }

    @Override
    protected void load(Configuration config) {
        config.pushSynchronized(this.isSynchronized());
        if (this.validator != null) {
            config.pushValidator(this::validationWrapper);
        }
        this.value = (float) config.getDouble(this.name, this.category, this.defaultValue, this.minValue, this.maxValue, this.comment);
    }

    @Override
    public String valueToString() {
        return Float.toString(this.value);
    }

    @Override
    public void parseFromString(String value) {
        try {
            float parsed = Float.parseFloat(value);
            this.value = parsed < this.minValue ? this.minValue : (parsed > this.maxValue ? this.maxValue : parsed);
        } catch (Exception e) {
            this.logGenericParserError(value);
        }
    }

    @Override
    public String toString() {
        return this.valueToString();
    }

    public static Builder builder(Omniconfig.Builder parent, String name, float defaultValue) {
        return new Builder(parent, name, defaultValue);
    }

    public static class Builder extends AbstractParameter.Builder<FloatParameter, Builder> {
        protected final float defaultValue;
        protected float minValue = 0, maxValue = OmniconfigCore.STANDART_INTEGER_LIMIT;
        protected Function<Float, Float> validator;

        protected Builder(Omniconfig.Builder parentBuilder, String name, float defaultValue) {
            super(parentBuilder, name);
            this.defaultValue = defaultValue;
        }

        public Builder max(float maxValue) {
            this.maxValue = maxValue;
            return this;
        }

        public Builder min(float minValue) {
            this.minValue = minValue;
            return this;
        }

        public Builder minMax(float minMax) {
            this.min(-minMax);
            this.max(minMax);
            return this;
        }

        public Builder validator(Function<Float, Float> validator) {
            this.validator = validator;
            return this;
        }

        @Override
        public FloatParameter build() {
            this.finishBuilding();
            return new FloatParameter(this);
        }
    }

}
