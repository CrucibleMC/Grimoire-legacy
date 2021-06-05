package io.github.crucible.omniconfig.wrappers.values;

import java.util.function.Consumer;

import io.github.crucible.omniconfig.OmniconfigCore;
import io.github.crucible.omniconfig.core.Configuration;

public class IntegerParameter extends AbstractParameter<IntegerParameter> {
    private int defaultValue;
    private int value;
    private int minValue = 0;
    private int maxValue = OmniconfigCore.STANDART_INTEGER_LIMIT;

    public IntegerParameter(int defaultValue) {
        super();
        this.defaultValue = defaultValue;
        this.value = this.defaultValue;
    }

    public int getDefaultValue() {
        return this.defaultValue;
    }

    public void setDefaultValue(int defaultValue) {
        this.defaultValue = defaultValue;
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getMaxValue() {
        return this.maxValue;
    }

    public int getMinValue() {
        return this.minValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    @Override
    public IntegerParameter invoke(Configuration config) {
        if (!this.isClientOnly() || config.getSidedType() == Configuration.SidedConfigType.CLIENT) {
            config.pushSynchronized(this.isSynchornized);
            this.value = config.getInt(this.name, this.category, this.defaultValue, this.minValue, this.maxValue, this.comment);
        }

        return super.invoke(config);
    }

    @Override
    public String valueToString() {
        return Integer.toString(this.value);
    }

    @Override
    public void parseFromString(String value) {
        try {
            int parsed = Integer.parseInt(value);
            this.value = parsed < this.minValue ? this.minValue : (parsed > this.maxValue ? this.maxValue : parsed);
        } catch (Exception e) {
            this.logGenericParserError(value);
        }
    }

    @Override
    public String toString() {
        return Integer.toString(this.value);
    }

}