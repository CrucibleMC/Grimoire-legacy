package io.github.crucible.omniconfig.wrappers.values;

import io.github.crucible.omniconfig.core.Configuration;

public class StringParameter extends AbstractParameter<StringParameter> {
    private String defaultValue;
    private String value;
    private String[] validValues;

    public StringParameter(String defaultValue) {
        super();
        this.defaultValue = defaultValue;
        this.value = this.defaultValue;

        this.validValues = null;
    }

    public String getDefaultValue() {
        return this.defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setValidValues(String... validValues) {
        this.validValues = validValues;
    }

    public String[] getValidValues() {
        return this.validValues;
    }

    @Override
    public StringParameter invoke(Configuration config) {
        if (!this.isClientOnly() || config.getSidedType() == Configuration.SidedConfigType.CLIENT) {
            config.pushSynchronized(this.isSynchornized);
            if (this.validValues == null) {
                this.value = config.getString(this.name, this.category, this.defaultValue, this.comment);
            } else {
                this.value = config.getString(this.name, this.category, this.defaultValue, this.comment, this.validValues);
            }
        }

        return super.invoke(config);
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

}