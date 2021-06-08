package io.github.crucible.omniconfig.wrappers.values;

import io.github.crucible.omniconfig.OmniconfigCore;
import io.github.crucible.omniconfig.core.Configuration;
import io.github.crucible.omniconfig.wrappers.Omniconfig;
import io.github.crucible.omniconfig.wrappers.values.BooleanParameter.Builder;

public class BooleanParameter extends AbstractParameter<BooleanParameter> {
    protected final boolean defaultValue;
    protected boolean value;

    public BooleanParameter(Builder builder) {
        super(builder);

        this.defaultValue = builder.defaultValue;
        this.finishConstruction(builder);
    }

    public boolean getValue() {
        return this.value;
    }

    public boolean getDefault() {
        return this.defaultValue;
    }

    @Override
    protected void load(Configuration config) {
        config.pushSynchronized(this.isSynchronized());
        this.value = config.getBoolean(this.name, this.category, this.defaultValue, this.comment);
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
        return this.valueToString();
    }

    public static Builder builder(Omniconfig.Builder parent, String name, boolean defaultValue) {
        return new Builder(parent, name, defaultValue);
    }

    public static class Builder extends AbstractParameter.Builder<BooleanParameter, Builder> {
        protected final boolean defaultValue;

        protected Builder(Omniconfig.Builder parentBuilder, String name, boolean defaultValue) {
            super(parentBuilder, name);
            this.defaultValue = defaultValue;
        }

        @Override
        public BooleanParameter build() {
            return new BooleanParameter(this);
        }
    }

}
