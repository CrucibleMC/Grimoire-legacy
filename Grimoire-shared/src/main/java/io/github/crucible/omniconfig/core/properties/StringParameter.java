package io.github.crucible.omniconfig.core.properties;

import java.util.List;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;

import io.github.crucible.omniconfig.api.builders.IStringPropertyBuilder;
import io.github.crucible.omniconfig.api.properties.IStringProperty;
import io.github.crucible.omniconfig.backing.Configuration;
import io.github.crucible.omniconfig.core.Omniconfig;

public class StringParameter extends AbstractParameter<IStringProperty> implements IStringProperty {
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

    @Override
    public String getValue() {
        this.assertValidEnvironment();
        return this.value;
    }

    @Override
    public List<String> getValidValues() {
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

    @Override
    protected boolean valueMatchesDefault(Configuration inConfig) {
        this.load(inConfig);
        return this.value.equals(this.defaultValue);
    }

    @Override
    protected boolean valuesMatchIn(Configuration one, Configuration two) {
        this.load(one);
        String valueOne = this.value;
        this.load(two);

        return valueOne.equals(this.value);
    }


    public static Builder builder(Omniconfig.Builder parent, String name, String defaultValue) {
        return new Builder(parent, name, defaultValue);
    }

    public static class Builder extends AbstractParameter.Builder<IStringProperty, Builder> implements IStringPropertyBuilder {
        protected final String defaultValue;
        protected ImmutableList.Builder<String> validValues;
        protected Function<String, String> validator;

        protected Builder(Omniconfig.Builder parentBuilder, String name, String defaultValue) {
            super(parentBuilder, name);

            this.defaultValue = defaultValue;
        }

        @Override
        public Builder validValues(String... values) {
            this.validValues = ImmutableList.builder();

            for (String value : values) {
                this.validValues.add(value);
            }

            return this;
        }

        @Override
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