package io.github.crucible.omniconfig.wrappers.values;

import io.github.crucible.omniconfig.core.Configuration;
import io.github.crucible.omniconfig.lib.Perhaps;

public class PerhapsParameter extends AbstractParameter<PerhapsParameter> {
    private Perhaps defaultValue;
    private Perhaps value;
    private double minValue = 0;
    private double maxValue = 100;

    public PerhapsParameter(int defaultValue) {
        super();
        this.defaultValue = Perhaps.fromPercent(defaultValue);
        this.value = this.defaultValue;
    }

    public Perhaps getDefaultValue() {
        return this.defaultValue;
    }

    public void setDefaultValue(double defaultPercentValue) {
        this.defaultValue = Perhaps.fromPercent(defaultPercentValue);
    }

    public Perhaps getValue() {
        return this.value;
    }

    public void setValue(double percentValue) {
        this.value = Perhaps.fromPercent(percentValue);
    }

    public double getMinValue() {
        return this.minValue;
    }

    public double getMaxValue() {
        return this.maxValue;
    }

    public void setMinValue(double minValue) {
        this.minValue = minValue;
    }

    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }

    @Override
    public PerhapsParameter invoke(Configuration config) {
        if (!this.isClientOnly() || config.getSidedType() == Configuration.SidedConfigType.CLIENT) {
            config.pushSynchronized(this.isSynchornized);
            this.value = Perhaps.fromPercent(config.getDouble(this.name, this.category, this.defaultValue.asPercent(), this.minValue, this.maxValue, this.comment));
        }

        return super.invoke(config);
    }

    @Override
    public String valueToString() {
        return Double.toString(this.value.asPercent());
    }

    @Override
    public void parseFromString(String value) {
        try {
            double parsed = Double.parseDouble(value);
            double percentage = parsed < this.minValue ? this.minValue : (parsed > this.maxValue ? this.maxValue : parsed);

            this.value = Perhaps.fromPercent(percentage);
        } catch (Exception e) {
            this.logGenericParserError(value);
        }
    }

    @Override
    public String toString() {
        return this.valueToString();
    }

}