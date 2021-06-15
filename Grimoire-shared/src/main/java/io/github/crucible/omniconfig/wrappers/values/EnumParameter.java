package io.github.crucible.omniconfig.wrappers.values;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;

import io.github.crucible.omniconfig.core.Configuration;
import io.github.crucible.omniconfig.wrappers.Omniconfig;

public class EnumParameter<T extends Enum<T>> extends AbstractParameter<EnumParameter<T>> {
    protected final Class<T> enumClass;
    protected final T defaultValue;
    protected final ImmutableList<T> validValues;
    protected final Function<T, T> validator;
    protected T value;

    public EnumParameter(Builder<T> builder) {
        super(builder);

        this.enumClass = builder.enumClass;
        this.defaultValue = builder.defaultValue;
        this.validator = builder.validator;

        ImmutableList.Builder<T> validBuilder;

        if (builder.validValues != null) {
            validBuilder = builder.validValues;
        } else {
            validBuilder = ImmutableList.builder();
            validBuilder.add(this.enumClass.getEnumConstants());
        }

        this.validValues = validBuilder.build();

        this.finishConstruction(builder);
    }

    public T getDefaultValue() {
        this.assertValidEnvironment();
        return this.defaultValue;
    }

    public T getValue() {
        this.assertValidEnvironment();
        return this.value;
    }

    public List<T> getValidValues() {
        this.assertValidEnvironment();
        return this.validValues;
    }

    protected String validationWrapper(String value) {
        T result = this.validator.apply(Enum.valueOf(this.enumClass, value));
        return result.name();
    }

    @Override
    protected void load(Configuration config) {
        config.pushSynchronized(this.isSynchronized);
        if (this.validator != null) {
            config.pushValidator(this::validationWrapper);
        }
        T[] checkType = Arrays.copyOf(this.enumClass.getEnumConstants(), 0);
        this.value = config.getEnum(this.name, this.category, this.defaultValue, this.comment, this.validValues.toArray(checkType));
    }

    @Override
    public String valueToString() {
        return this.value.toString();
    }

    @Override
    public void parseFromString(String value) {
        this.value = Enum.valueOf(this.enumClass, value);
    }

    @Override
    public String toString() {
        return this.value.toString();
    }

    @Override
    protected boolean valueMatchesDefault(Configuration inConfig) {
        this.load(inConfig);
        return this.value == this.defaultValue;
    }

    @Override
    protected boolean valuesMatchIn(Configuration one, Configuration two) {
        this.load(one);
        T valueOne = this.value;
        this.load(two);

        return valueOne == this.value;
    }


    public static <T extends Enum<T>> Builder<T> builder(Omniconfig.Builder parent, String name, T defaultValue) {
        return new Builder<>(parent, name, defaultValue);
    }

    public static class Builder<T extends Enum<T>> extends AbstractParameter.Builder<EnumParameter<T>, Builder<T>> {
        protected final T defaultValue;
        protected final Class<T> enumClass;
        protected ImmutableList.Builder<T> validValues = null;
        protected Function<T, T> validator = null;

        protected Builder(Omniconfig.Builder parentBuilder, String name, T defaultValue) {
            super(parentBuilder, name);

            this.defaultValue = defaultValue;
            this.enumClass = defaultValue.getDeclaringClass();
        }

        @SuppressWarnings("unchecked")
        public Builder<T> validValues(T... values) {
            this.validValues = ImmutableList.builder();

            for (T value : values) {
                this.validValues.add(value);
            }

            return this;
        }

        public Builder<T> validator(Function<T, T> validator) {
            this.validator = validator;
            return this;
        }

        @Override
        public EnumParameter<T> build() {
            this.finishBuilding();
            return new EnumParameter<>(this);
        }

    }

}