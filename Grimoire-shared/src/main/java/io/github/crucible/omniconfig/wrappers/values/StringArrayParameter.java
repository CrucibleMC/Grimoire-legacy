package io.github.crucible.omniconfig.wrappers.values;

import io.github.crucible.omniconfig.core.Configuration;
import io.github.crucible.omniconfig.lib.Perhaps;

public class StringArrayParameter extends AbstractParameter<StringArrayParameter> {
    private String[] defaultValue;
    private String[] validValues;
    private String[] value;

    public StringArrayParameter(String... defaultValue) {
        super();
        this.defaultValue = defaultValue;
        this.value = this.defaultValue;

        this.validValues = null;
    }

    public String[] getDefaultValue() {
        return this.defaultValue;
    }

    public void setDefaultValue(String... defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String[] getValue() {
        return this.value;
    }

    public void setValue(String[] value) {
        this.value = value;
    }

    public void setValidValues(String... validValues) {
        this.validValues = validValues;
    }

    public String[] getValidValues() {
        return this.validValues;
    }

    @Override
    public StringArrayParameter invoke(Configuration config) {
        if (!this.isClientOnly() || config.getSidedType() == Configuration.SidedConfigType.CLIENT) {
            config.pushSynchronized(this.isSynchornized);

            if (this.validValues == null) {
                this.value = config.getStringList(this.name, this.category, this.defaultValue, this.comment);
            } else {
                this.value = config.getStringList(this.name, this.category, this.defaultValue, this.comment, this.validValues);
            }
        }

        return super.invoke(config);
    }

    @Override
    public String valueToString() {
        String stringValue = null;

        for (String string : this.value) {
            if (stringValue == null) {
                stringValue = string;
            } else {
                stringValue += "[$@$]" + string;
            }

        }

        return stringValue;
    }

    @Override
    public void parseFromString(String value) {
        try {
            String[] stringValues = value.split("[$@$]");
            this.value = stringValues;
        } catch (Exception e) {
            this.logGenericParserError(value);
        }
    }

    @Override
    public String toString() {
        return this.valueToString();
    }


}