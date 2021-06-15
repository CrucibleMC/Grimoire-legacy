package io.github.crucible.omniconfig.wrappers.values;

import java.util.function.Function;

import io.github.crucible.omniconfig.OmniconfigCore;
import io.github.crucible.omniconfig.core.Configuration;
import io.github.crucible.omniconfig.lib.Perhaps;
import io.github.crucible.omniconfig.wrappers.Omniconfig;

public class PerhapsParameter extends AbstractParameter<PerhapsParameter> {
    protected final Perhaps defaultValue;
    protected final double minValue, maxValue;
    protected final Function<Perhaps, Perhaps> validator;
    protected Perhaps value;

    public PerhapsParameter(Builder builder) {
        super(builder);
        this.defaultValue = builder.defaultValue;
        this.minValue = builder.minValue;
        this.maxValue = builder.maxValue;
        this.validator = builder.validator;

        this.finishConstruction(builder);
    }

    public Perhaps getDefault() {
        this.assertValidEnvironment();
        return this.defaultValue;
    }

    public Perhaps getValue() {
        this.assertValidEnvironment();
        return this.value;
    }

    public double getMin() {
        this.assertValidEnvironment();
        return this.minValue;
    }

    public double getMax() {
        this.assertValidEnvironment();
        return this.maxValue;
    }

    protected Double validationWrapper(Double value) {
        Perhaps result = this.validator.apply(Perhaps.fromPercent(value));
        return result.asPercent();
    }

    @Override
    protected void load(Configuration config) {
        config.pushSynchronized(this.isSynchronized);
        if (this.validator != null) {
            config.pushValidator(this::validationWrapper);
        }
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

    @Override
    protected boolean valueMatchesDefault(Configuration inConfig) {
        this.load(inConfig);
        return this.value.equals(this.defaultValue);
    }

    @Override
    protected boolean valuesMatchIn(Configuration one, Configuration two) {
        this.load(one);
        Perhaps valueOne = this.value;
        this.load(two);

        return valueOne.equals(this.value);
    }


    public static Builder builder(Omniconfig.Builder parentBuilder, String name, Perhaps defaultValue) {
        return new Builder(parentBuilder, name, defaultValue);
    }

    public static class Builder extends AbstractParameter.Builder<PerhapsParameter, Builder> {
        protected final Perhaps defaultValue;
        protected double minValue = 0, maxValue = 100;
        protected Function<Perhaps, Perhaps> validator;

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

        public Builder validator(Function<Perhaps, Perhaps> validator) {
            this.validator = validator;
            return this;
        }

        @Override
        public PerhapsParameter build() {
            this.finishBuilding();
            return new PerhapsParameter(this);
        }

    }

}