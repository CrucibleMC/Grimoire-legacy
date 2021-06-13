package io.github.crucible.omniconfig.wrappers.values;

import java.util.List;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;

import io.github.crucible.omniconfig.core.Configuration;
import io.github.crucible.omniconfig.lib.Perhaps;
import io.github.crucible.omniconfig.wrappers.Omniconfig;

public class StringArrayParameter extends AbstractParameter<StringArrayParameter> {
    protected final ImmutableList<String> defaultValue;
    protected final ImmutableList<String> validValues;
    protected final Function<String[], String[]> validator;
    protected ImmutableList<String> value;

    public StringArrayParameter(Builder builder) {
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


    public ImmutableList<String> getDefaultValue() {
        this.assertValidEnvironment();
        return this.defaultValue;
    }

    public ImmutableList<String> getValue() {
        this.assertValidEnvironment();
        return this.value;
    }

    public String[] getArrayValue() {
        this.assertValidEnvironment();
        return this.value.toArray(new String[0]);
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
            this.value = fromArray(config.getStringList(this.name, this.category, toArray(this.defaultValue), this.comment));
        } else {
            this.value = fromArray(config.getStringList(this.name, this.category, toArray(this.defaultValue), this.comment, toArray(this.validValues)));
        }
    }

    @Override
    public String valueToString() {
        String stringValue = null;

        for (String string : this.value) {
            if (stringValue == null) {
                stringValue = string;
            } else {
                stringValue += "@%$" + string;
            }

        }

        return stringValue;
    }

    @Override
    public void parseFromString(String value) {
        try {
            String[] stringValues = value.split("@%$");
            this.value = fromArray(stringValues);
        } catch (Exception e) {
            this.logGenericParserError(value);
        }
    }

    @Override
    public String toString() {
        return this.valueToString();
    }


    protected static ImmutableList<String> fromArray(String... array) {
        ImmutableList.Builder<String> builder = ImmutableList.builder();
        builder.add(array);
        return builder.build();
    }

    protected static String[] toArray(List<String> list) {
        return list.toArray(new String[0]);
    }

    public static Builder builder(Omniconfig.Builder parentBuilder, String name, String... defaultValue) {
        return new Builder(parentBuilder, name, defaultValue);
    }

    public static class Builder extends AbstractParameter.Builder<StringArrayParameter, Builder> {
        protected final ImmutableList<String> defaultValue;
        protected ImmutableList.Builder<String> validValues;
        protected Function<String[], String[]> validator;

        protected Builder(Omniconfig.Builder parentBuilder, String name, String... defaultValue) {
            super(parentBuilder, name);

            ImmutableList.Builder<String> listBuilder = ImmutableList.builder();
            this.defaultValue = listBuilder.add(defaultValue).build();
        }

        public Builder validValues(String... values) {
            this.validValues = ImmutableList.builder();
            this.validValues.add(values);
            return this;
        }

        public Builder validator(Function<String[], String[]> validator) {
            this.validator = validator;
            return this;
        }

        @Override
        public StringArrayParameter build() {
            this.finishBuilding();
            return new StringArrayParameter(this);
        }

    }
}