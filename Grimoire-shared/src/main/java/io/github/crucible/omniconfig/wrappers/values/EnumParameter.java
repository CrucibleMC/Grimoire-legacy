package io.github.crucible.omniconfig.wrappers.values;

import io.github.crucible.omniconfig.core.Configuration;

public class EnumParameter<T extends Enum<T>> extends AbstractParameter<EnumParameter<T>> {
    private final Class<T> clazz;
    private T defaultValue;
    private T[] validValues;
    private T value;

    public EnumParameter(T defaultValue) {
        super();
        this.clazz = defaultValue.getDeclaringClass();
        this.defaultValue = defaultValue;
        this.value = this.defaultValue;

        this.validValues = this.clazz.getEnumConstants();
    }

    public T getDefaultValue() {
        return this.defaultValue;
    }

    public void setDefaultValue(T defaultValue) {
        this.defaultValue = defaultValue;
    }

    public T getValue() {
        return this.value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    @SuppressWarnings("unchecked")
    public void setValidValues(T... values) {
        this.validValues = values;
    }

    public T[] getValidValues() {
        return this.validValues;
    }

    @Override
    public EnumParameter<T> invoke(Configuration config) {
        // <V extends Enum<V>> ForgeConfigSpec
        if (!this.isClientOnly() || config.getSidedType() == Configuration.SidedConfigType.CLIENT) {
            config.pushSynchronized(this.isSynchornized);
            this.value = config.getEnum(this.name, this.category, this.defaultValue, this.comment, this.validValues);
        }

        return super.invoke(config);
    }

    @Override
    public String valueToString() {
        return this.value.toString();
    }

    @Override
    public void parseFromString(String value) {
        this.value = Enum.valueOf(this.clazz, value);
    }

    @Override
    public String toString() {
        return this.value.toString();
    }

}