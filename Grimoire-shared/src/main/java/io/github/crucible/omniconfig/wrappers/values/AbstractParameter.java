package io.github.crucible.omniconfig.wrappers.values;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import io.github.crucible.omniconfig.OmniconfigCore;
import io.github.crucible.omniconfig.core.Configuration;

public abstract class AbstractParameter<T extends AbstractParameter<T>> {
    protected String name = "unknownKey";
    protected String comment = "undefinedComment";
    protected String category = "undefinedCategory";
    protected boolean isSynchornized = false;
    protected boolean clientOnly = false;

    protected List<Consumer<T>> onInvokeList = new ArrayList<>();

    public AbstractParameter() {
        // NO-OP
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return this.category;
    }

    public String getName() {
        return this.name;
    }

    public String getComment() {
        return this.comment;
    }

    public boolean isSynchronized() {
        return this.isSynchornized;
    }

    public String getId() {
        return this.category + "$" + this.name;
    }

    public void setSynchronized(boolean isSyncable) {
        this.isSynchornized = isSyncable;
    }

    public boolean isClientOnly() {
        return this.clientOnly;
    }

    public void setClientOnly(boolean clientOnly) {
        this.clientOnly = clientOnly;
    }

    protected void logGenericParserError(String value) {
        OmniconfigCore.logger.error("Error when parsing value of '" + this.name + "' in '" + this.category + "': " + value);
    }

    public abstract String valueToString();

    public abstract void parseFromString(String value);

    public void uponInvoke(Consumer<T> consumer) {
        this.uponInvoke(consumer, true);
    }

    @SuppressWarnings("unchecked")
    public void uponInvoke(Consumer<T> consumer, boolean initialInvokation) {
        this.onInvokeList.add(consumer);

        if (initialInvokation) {
            consumer.accept((T) this);
        }
    }

    @SuppressWarnings("unchecked")
    public T invoke(Configuration config) {
        this.onInvokeList.forEach(consumer -> {
            consumer.accept((T) this);
        });
        return (T) this;
    }

}