package io.github.crucible.omniconfig.wrappers.values;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import javax.print.attribute.HashAttributeSet;

import com.google.common.collect.ImmutableList;

import io.github.crucible.omniconfig.OmniconfigCore;
import io.github.crucible.omniconfig.core.Configuration;
import io.github.crucible.omniconfig.wrappers.Omniconfig;

@SuppressWarnings("unchecked")
public abstract class AbstractParameter<T extends AbstractParameter<T>> {
    protected final String name;
    protected final String comment;
    protected final String category;
    protected final boolean isSynchronized;
    protected final ImmutableList<Listener<T>> listeners;

    protected AbstractParameter(Builder<T, ?> builder) {
        this.name = builder.prefix + builder.name;
        this.comment = builder.comment;
        this.category = builder.category;
        this.isSynchronized = builder.isSynchronized;
        this.listeners = builder.listeners.build();
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
        return this.isSynchronized;
    }

    public String getID() {
        return this.category + "$" + this.name;
    }

    protected void logGenericParserError(String value) {
        OmniconfigCore.logger.error("Error when parsing value of '" + this.name + "' in '" + this.category + "': " + value);
    }

    public abstract String valueToString();

    public abstract void parseFromString(String value);

    protected abstract void load(Configuration config);


    public void reloadFrom(Omniconfig config) {
        this.load(config.getBackingConfig());
        this.notifyListeners();
    }

    protected void finishConstruction(Builder<T, ?> builder) {
        this.load(builder.getBackingConfig());
        this.notifyListeners();

        builder.getParentBuilder().getPropertyMap().put(this.getID(), this);
    }

    protected void notifyListeners() {
        this.listeners.forEach(listener -> {
            listener.accept((T) this);
        });
    }

    protected static class Listener<E extends AbstractParameter<E>> {
        private final Consumer<E> consumer;
        private boolean firstLoadPassed;

        public Listener(Consumer<E> consumer, boolean invokeOnFirstLoad) {
            this.firstLoadPassed = invokeOnFirstLoad;
            this.consumer = consumer;
        }

        public void accept(E value) {
            if (!this.firstLoadPassed) {
                this.firstLoadPassed = true;
            } else {
                this.consumer.accept(value);
            }
        }
    }

    public static abstract class Builder<E extends AbstractParameter<E>, T extends Builder<E, T>> {
        protected final ImmutableList.Builder<Listener<E>> listeners = ImmutableList.builder();
        protected final Omniconfig.Builder parentBuilder;
        protected final String name;

        protected String comment = "undefinedComment";
        protected boolean isSynchronized;
        protected String category;
        protected String prefix;

        protected Builder(Omniconfig.Builder parentBuilder, String name) {
            this.parentBuilder = parentBuilder;
            this.isSynchronized = parentBuilder.isSynchronized();
            this.prefix = parentBuilder.getPrefix();
            this.category = parentBuilder.getCurrentCategory();
            this.name = name;
        }

        protected Configuration getBackingConfig() {
            return this.parentBuilder.getBackingConfig();
        }

        protected Omniconfig.Builder getParentBuilder() {
            return this.parentBuilder;
        }

        public T category(String category) {
            this.category = category;
            return this.self();
        }

        public T comment(String comment) {
            this.comment = comment;
            return this.self();
        }

        public T sync(boolean isSyncable) {
            this.isSynchronized = isSyncable;
            return this.self();
        }

        public T sync() {
            this.sync(true);
            return this.self();
        }

        public T uponLoad(Consumer<E> consumer, boolean invokeOnFirstLoad) {
            this.listeners.add(new Listener<>(consumer, invokeOnFirstLoad));
            return this.self();
        }

        public T uponLoad(Consumer<E> consumer) {
            this.uponLoad(consumer, true);
            return this.self();
        }

        protected T self() {
            return (T) this;
        }

        public abstract E build();
    }

}