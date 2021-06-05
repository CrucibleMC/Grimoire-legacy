package io.github.crucible.omniconfig.wrappers.values;

import io.github.crucible.omniconfig.OmniconfigCore;
import io.github.crucible.omniconfig.core.Configuration;

public class DoubleParameter extends AbstractParameter<DoubleParameter> {
    private double defaultValue;
    private double value;
    private double minValue = 0;
    private double maxValue = OmniconfigCore.STANDART_INTEGER_LIMIT;

    public DoubleParameter(double defaultValue) {
        super();
        this.defaultValue = defaultValue;
        this.value = this.defaultValue;
    }

    public double getDefaultValue() {
        return this.defaultValue;
    }

    public void setDefaultValue(double defaultValue) {
        this.defaultValue = defaultValue;
    }

    public double getValue() {
        return this.value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getMaxValue() {
        return this.maxValue;
    }

    public double getMinValue() {
        return this.minValue;
    }

    public void setMinValue(double minValue) {
        this.minValue = minValue;
    }

    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }

    @Override
    public DoubleParameter invoke(Configuration config) {
        if (!this.isClientOnly() || config.getSidedType() == Configuration.SidedConfigType.CLIENT) {
            config.pushSynchronized(this.isSynchornized);
            this.value = config.getDouble(this.name, this.category, this.defaultValue, this.minValue, this.maxValue, this.comment);
        }

        return super.invoke(config);
    }

    @Override
    public String valueToString() {
        return Double.toString(this.value);
    }

    @Override
    public void parseFromString(String value) {
        try {
            double parsed = Double.parseDouble(value);
            this.value = parsed < this.minValue ? this.minValue : (parsed > this.maxValue ? this.maxValue : parsed);
        } catch (Exception e) {
            this.logGenericParserError(value);
        }
    }

    @Override
    public String toString() {
        return Double.toString(this.value);
    }

}