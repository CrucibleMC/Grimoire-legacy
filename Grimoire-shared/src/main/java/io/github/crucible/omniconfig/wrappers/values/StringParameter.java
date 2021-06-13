package io.github.crucible.omniconfig.wrappers.values;

import java.util.List;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;

import io.github.crucible.omniconfig.core.Configuration;
import io.github.crucible.omniconfig.wrappers.Omniconfig;

public class StringParameter extends AbstractParameter<StringParameter> {
    protected final String defaultValue;
    protected final ImmutableList<String> validValues;
    protected final Function<String, String> validator;
    protected String value;

    public StringParameter(Builder builder) {
        super(builder);
        this.defaultValue = builder.defaultValue;
        this.validator = builder.validator;

        ImmutableList.Builder<String> validBuilder;

        if (builder.validValues != null) {
            validBuilder = builder.validValues;
        } else {
            validBuilder = ImmutableList.builder();
        }

        this.validValues = validBuilder.build();
        this.finishConstruction(builder);
    }

    public String getValue() {
        this.assertValidEnvironment();
        return this.value;
    }

    public ImmutableList<String> getValidValues() {
        this.assertValidEnvironment();
        return this.validValues;
    }

    @Override
    protected void load(Configuration config) {
        config.pushSynchronized(this.isSynchronized);
        if (this.validator != null) {
            config.pushValidator(this.validator);
        }

        if (this.validValues.size() <= 0) {
            this.value = config.getString(this.name, this.category, this.defaultValue, this.comment, null);
        } else {
            this.value = config.getString(this.name, this.category, this.defaultValue, this.comment, this.validValues.toArray(new String[0]));
        }
    }

    @Override
    public String valueToString() {
        return this.value;
    }

    @Override
    public void parseFromString(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public static Builder builder(Omniconfig.Builder parent, String name, String defaultValue) {
        return new Builder(parent, name, defaultValue);
    }

    public static class Builder extends AbstractParameter.Builder<StringParameter, Builder> {
        protected final String defaultValue;
        protected ImmutableList.Builder<String> validValues;
        protected Function<String, String> validator;

        protected Builder(Omniconfig.Builder parentBuilder, String name, String defaultValue) {
            super(parentBuilder, name);

            this.defaultValue = defaultValue;
        }

        public Builder validValues(String... values) {
            this.validValues = ImmutableList.builder();

            for (String value : values) {
                this.validValues.add(value);
            }

            return this;
        }

        public Builder validator(Function<String, String> validator) {
            this.validator = validator;
            return this;
        }

        @Override
        public StringParameter build() {
            this.finishBuilding();
            return new StringParameter(this);
        }

    }
}