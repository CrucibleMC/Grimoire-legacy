package io.github.crucible.omniconfig.wrappers.values;

import io.github.crucible.omniconfig.core.Configuration;

public class BooleanParameter extends AbstractParameter<BooleanParameter> {
    private boolean defaultValue;
    private boolean value;

    public BooleanParameter(boolean defaultValue) {
        super();
        this.defaultValue = defaultValue;
        this.value = this.defaultValue;
    }

    public boolean getDefaultValue() {
        return this.defaultValue;
    }

    public void setDefaultValue(boolean defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean getValue() {
        return this.value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    @Override
    public BooleanParameter invoke(Configuration config) {
        if (!this.isClientOnly() || config.getSidedType() == Configuration.SidedConfigType.CLIENT) {
            config.pushSynchronized(this.isSynchornized);
            this.value = config.getBoolean(this.name, this.category, this.defaultValue, this.comment);
        }

        return super.invoke(config);
    }

    @Override
    public String valueToString() {
        return Boolean.toString(this.value);
    }

    @Override
    public void parseFromString(String value) {
        try {
            this.value = Boolean.parseBoolean(value);
        } catch (Exception e) {
            this.logGenericParserError(value);
        }
    }

    @Override
    public String toString() {
        return Boolean.toString(this.value);
    }

}