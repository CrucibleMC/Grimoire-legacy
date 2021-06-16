package io.github.crucible.omniconfig.annotation.lib;

import com.google.common.base.Preconditions;

import io.github.crucible.omniconfig.OmniconfigCore;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ClassSet<T> implements Iterable<Class<? extends T>> {
    private final Set<Class<? extends T>> classes = new HashSet<>();
    private final Class<T> baseClass;

    public ClassSet(@NotNull Class<T> baseClass) {
        this.baseClass = baseClass;
        Preconditions.checkArgument(baseClass != Class.class, "baseClass must not be java.lang.Class");
    }

    public boolean isEmpty() {
        return this.classes.isEmpty();
    }

    public boolean contains(@Nullable T instance) {
        return instance != null && this.contains((Class<? extends T>) instance.getClass());
    }

    public boolean contains(@NotNull Class<? extends T> clazz) {
        return this.contains(clazz, true);
    }

    public boolean contains(@Nullable T instance, boolean checkHierarchy) {
        return instance != null && this.contains((Class<? extends T>) instance.getClass(), checkHierarchy);
    }

    public boolean contains(@NotNull Class<? extends T> clazz, boolean checkHierarchy) {
        if (this.baseClass.isAssignableFrom(clazz)) {
            if (this.classes.contains(clazz))
                return true;

            if (checkHierarchy) {
                for (Class<? extends T> aClass : this.classes) {
                    if (aClass.isAssignableFrom(clazz))
                        return true;
                }
            }
        }

        return false;
    }

    @Override
    public Iterator<Class<? extends T>> iterator() {
        return this.classes.iterator();
    }

    public void clear() {
        this.classes.clear();
    }

    public boolean add(@NotNull Class<? extends T> clazz) {
        return this.baseClass.isAssignableFrom(clazz) && this.classes.add(clazz);
    }

    public void addRaw(@NotNull Collection<String> classNames) {
        classNames.forEach(this::addRaw);
    }

    public void addRaw(@NotNull String className) {
        try {
            Class<?> clazz = Class.forName(className);
            if (this.baseClass.isAssignableFrom(clazz)) {
                this.add((Class<? extends T>) clazz);
            }
            OmniconfigCore.logger.warn("Class {} is not assignable from {}", className, this.baseClass.getName());
        } catch (ClassNotFoundException e) {
            OmniconfigCore.logger.warn("Class {} not found", className);
        }
    }

    public Set<String> getRaw() {
        return this.classes.stream().map(Class::getName).collect(Collectors.toSet());
    }
}
