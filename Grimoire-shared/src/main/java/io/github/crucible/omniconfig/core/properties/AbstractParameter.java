package io.github.crucible.omniconfig.core.properties;

import java.util.function.Consumer;

import com.google.common.collect.ImmutableList;

import io.github.crucible.grimoire.common.GrimoireCore;
import io.github.crucible.omniconfig.OmniconfigCore;
import io.github.crucible.omniconfig.api.core.SidedConfigType;
import io.github.crucible.omniconfig.api.core.VersioningPolicy;
import io.github.crucible.omniconfig.api.properties.IAbstractProperty;
import io.github.crucible.omniconfig.backing.Configuration;
import io.github.crucible.omniconfig.core.Omniconfig;

@SuppressWarnings({"unchecked", "rawtypes"})
public abstract class AbstractParameter<T extends IAbstractProperty> implements IAbstractProperty {
    protected final String name;
    protected final String comment;
    protected final String category;
    protected final boolean isSynchronized;
    protected final ImmutableList<Listener<T>> listeners;
    protected final SidedConfigType sidedType;
    protected final boolean isValidEnvironment;

    protected AbstractParameter(Builder<T, ?> builder) {
        this.name = builder.prefix + builder.name;
        this.comment = builder.comment;
        this.category = builder.category;
        this.isSynchronized = builder.isSynchronized;
        this.listeners = builder.listeners.build();
        this.sidedType = builder.sidedType;

        this.isValidEnvironment = !this.sidedType.isSided() || this.sidedType.getSide() == GrimoireCore.INSTANCE.getEnvironment();
    }

    @Override
    public String getCategory() {
        return this.category;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getComment() {
        return this.comment;
    }

    @Override
    public boolean isSynchronized() {
        return this.isSynchronized;
    }

    @Override
    public String getID() {
        return this.category + "$" + this.name;
    }

    protected void logGenericParserError(String value) {
        OmniconfigCore.logger.error("Error when parsing value of '" + this.name + "' in '" + this.category + "': " + value);
    }

    public abstract String valueToString();

    public abstract void parseFromString(String value);

    protected abstract void load(Configuration config);

    protected abstract boolean valuesMatchIn(Configuration one, Configuration two);

    protected abstract boolean valueMatchesDefault(Configuration inConfig);

    public void reloadFrom(Omniconfig config) {
        this.sidedType.executeSided(() -> {
            this.load(config.getBackingConfig());
            this.notifyListeners();
        });
    }

    protected void finishConstruction(Builder<T, ?> builder) {
        this.sidedType.executeSided(() -> {
            Omniconfig.Builder parentBuilder = builder.getParentBuilder();

            if (parentBuilder.updatingOldConfig()) {
                Configuration currentFile = parentBuilder.getBackingConfig();
                Configuration oldDefaultFile = parentBuilder.getDefaultConfigCopy();

                if (!this.valueMatchesDefault(oldDefaultFile)) {
                    if (currentFile.getVersioningPolicy() == VersioningPolicy.RESPECTFUL) {
                        OmniconfigCore.logger.info("Default value of property {} was updated in newest config version, discarding old property...", this.getID());
                        currentFile.tryRemoveProperty(this.category, this.name);
                    } else if (currentFile.getVersioningPolicy() == VersioningPolicy.NOBLE) {
                        if (this.valuesMatchIn(currentFile, oldDefaultFile)) {
                            OmniconfigCore.logger.info("Default value of property {} was updated in newest config version and value of that property was not modified by user. Discarding old property...", this.getID());
                            currentFile.tryRemoveProperty(this.category, this.name);
                        }
                    }
                }
            }

            this.load(parentBuilder.getBackingConfig());
            this.notifyListeners();
        });
        builder.getParentBuilder().getPropertyMap().put(this.getID(), this);
    }

    protected void notifyListeners() {
        this.sidedType.executeSided(() -> {
            this.listeners.forEach(listener -> {
                listener.accept((T) this);
            });
        });
    }

    protected void assertValidEnvironment() {
        if (!this.isValidEnvironment)
            throw new IllegalAccessError("Attempted to acess config property " + this.getID()
            + " of sided type " + this.sidedType
            + " in invalid environment " + GrimoireCore.INSTANCE.getEnvironment()
            + "!");
    }

    protected static class Listener<E extends IAbstractProperty> {
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

    public static abstract class Builder<E extends IAbstractProperty, T extends Builder<E, T>> {
        protected final ImmutableList.Builder<Listener<E>> listeners = ImmutableList.builder();
        protected final Omniconfig.Builder parentBuilder;
        protected final SidedConfigType sidedType;
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
            this.sidedType = parentBuilder.getBackingConfig().getSidedType();
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

        // Internal non-API methods


        protected Configuration getBackingConfig() {
            return this.parentBuilder.getBackingConfig();
        }

        protected Omniconfig.Builder getParentBuilder() {
            return this.parentBuilder;
        }

        protected void finishBuilding() {
            this.parentBuilder.markBuilderCompleted(this);
        }

        public String getParameterID() {
            return this.category + "$" + this.name;
        }
    }

}