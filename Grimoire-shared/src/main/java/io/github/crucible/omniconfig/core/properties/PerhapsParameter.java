package io.github.crucible.omniconfig.core.properties;

import java.util.function.Function;

import io.github.crucible.omniconfig.api.builders.IPerhapsPropertyBuilder;
import io.github.crucible.omniconfig.api.lib.Perhaps;
import io.github.crucible.omniconfig.api.properties.IPerhapsProperty;
import io.github.crucible.omniconfig.backing.Configuration;
import io.github.crucible.omniconfig.core.Omniconfig;

public class PerhapsParameter extends AbstractParameter<IPerhapsProperty> implements IPerhapsProperty {
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

    @Override
    public Perhaps getDefault() {
        this.assertValidEnvironment();
        return this.defaultValue;
    }

    @Override
    public Perhaps getValue() {
        this.assertValidEnvironment();
        return this.value;
    }

    @Override
    public Perhaps getMin() {
        this.assertValidEnvironment();
        return Perhaps.fromPercent(this.minValue);
    }

    @Override
    public Perhaps getMax() {
        this.assertValidEnvironment();
        return Perhaps.fromPercent(this.maxValue);
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

    public static class Builder extends AbstractParameter.Builder<IPerhapsProperty, Builder> implements IPerhapsPropertyBuilder {
        protected final Perhaps defaultValue;
        protected double minValue = 0, maxValue = 100;
        protected Function<Perhaps, Perhaps> validator;

        protected Builder(Omniconfig.Builder parentBuilder, String name, Perhaps defaultValue) {
            super(parentBuilder, name);
            this.defaultValue = defaultValue;
        }

        @Override
        public Builder max(double percent) {
            this.maxValue = percent;
            return this;
        }

        @Override
        public Builder min(double percent) {
            this.minValue = percent;
            return this;
        }

        @Override
        public Builder minMax(double percent) {
            this.min(-percent);
            this.max(percent);
            return this;
        }

        @Override
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