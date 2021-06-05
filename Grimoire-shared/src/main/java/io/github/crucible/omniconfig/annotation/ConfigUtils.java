package io.github.crucible.omniconfig.annotation;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import io.github.crucible.omniconfig.OmniconfigCore;
import io.github.crucible.omniconfig.wrappers.OmniconfigWrapper;

import org.apache.logging.log4j.core.config.Property;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public final class ConfigUtils {
    /*
    private static final String PACKAGE_DEFAULT = "default";
    private static final Set<Class<?>> LOADED_CONFIG_CLASSES = new HashSet<>();

    @NotNull
    public static OmniconfigWrapper getConfig(@NotNull Class<?> configClass) {
        return getConfig(getConfigName(configClass));
    }

    @NotNull
    private static String getConfigName(@NotNull Class<?> configClass) {
        Config annotation = configClass.getAnnotation(Config.class);
        Objects.requireNonNull(annotation, "Annotaion " + Config.class.getName() + " not found for class " + configClass.getName());
        String cfgName = annotation.name();
        if (Strings.isNullOrEmpty(cfgName)) {
            cfgName = getPackageName(configClass.getName());
        }
        Preconditions.checkArgument(!Strings.isNullOrEmpty(cfgName), "Config name for class " + configClass.getName() + " is not determined");
        return cfgName;
    }

    @NotNull
    public static OmniconfigWrapper getConfig(@NotNull String cfgName) {
        OmniconfigWrapper wrapper = OmniconfigWrapper.setupBuilder(cfgName, true, "1.0.0");
        return wrapper;
    }

    public static void readConfig(@NotNull Class<?> configClass) {
        readConfig(configClass, false);
    }

    public static void readConfig(@NotNull Class<?> configClass, @NotNull String configName) {
        readConfig(configClass, configName, false);
    }

    public static void readConfig(@NotNull Class<?> configClass, boolean reload) {
        readConfig(configClass, getConfigName(configClass), reload);
    }

    public static void readConfig(@NotNull Class<?> configClass, @NotNull String configName, boolean reload) {
        if (!LOADED_CONFIG_CLASSES.add(configClass) && !reload)
            return;

        OmniconfigWrapper cfg = getConfig(configName);

        try {
            for (Field field : configClass.getDeclaredFields()) {
                try {
                    readConfigProperty(cfg, field);
                } catch (Throwable throwable) {
                    OmniconfigCore.logger.error("Failed reading property {} in config {}", field, cfg.getConfigFile().getName(), throwable);
                }
            }

            for (Method method : configClass.getDeclaredMethods()) {
                try {
                    invokeConfigLoadCallback(cfg, method);
                } catch (Throwable throwable) {
                    OmniconfigCore.logger.error("Failed callback {} invocation in config {}", method, cfg.getConfigFile().getName(), throwable);
                }
            }
        } catch (Throwable throwable) {
            OmniconfigCore.logger.error("Failed reading config {}", cfg.getConfigFile().getName(), throwable);
        }

        cfg.save();
    }

    private static void readConfigProperty(@NotNull OmniconfigWrapper cfg, @NotNull Field field) throws IllegalAccessException {
        if (Modifier.isStatic(field.getModifiers())) {
            field.setAccessible(true);
            for (Annotation declaredAnnotation : field.getDeclaredAnnotations()) {
                // Handle all annotations to throw expection if field have multiple config annotations

                Class<? extends Annotation> annotationType = declaredAnnotation.annotationType();
                if (annotationType == ConfigBoolean.class) {
                    checkType(field, boolean.class);
                    checkNotFinal(field);

                    ConfigBoolean annotation = (ConfigBoolean) declaredAnnotation;
                    String name = annotation.name().isEmpty() ? field.getName() : annotation.name();

                    boolean defaultValue = field.getBoolean(null);
                    boolean value = cfg.getBoolean(name, annotation.category(), defaultValue, annotation.comment());
                    field.setBoolean(null, value);
                } else if (annotationType == ConfigFloat.class) {
                    checkType(field, float.class);
                    checkNotFinal(field);

                    ConfigFloat annotation = (ConfigFloat) declaredAnnotation;
                    String name = annotation.name().isEmpty() ? field.getName() : annotation.name();

                    float defaultValue = field.getFloat(null);
                    float value = (float) cfg.getDouble(name, annotation.category(), defaultValue, annotation.min(), annotation.max(), annotation.comment());
                    field.setFloat(null, value);
                } else if (annotationType == ConfigInt.class) {
                    checkType(field, int.class);
                    checkNotFinal(field);

                    ConfigInt annotation = (ConfigInt) declaredAnnotation;
                    String name = annotation.name().isEmpty() ? field.getName() : annotation.name();

                    int defaultValue = field.getInt(null);
                    int value = cfg.getInt(name, annotation.category(), defaultValue, annotation.min(), annotation.max(), annotation.comment());
                    field.setInt(null, value);
                } else if (annotationType == ConfigString.class) {
                    checkType(field, String.class);
                    checkNotFinal(field);

                    ConfigString annotation = (ConfigString) declaredAnnotation;
                    String name = annotation.name().isEmpty() ? field.getName() : annotation.name();

                    String defaultValue = (String) field.get(null);
                    String value = cfg.getString(name, annotation.category(), defaultValue, annotation.comment());
                    field.set(null, value);
                } else if (annotationType == ConfigClassSet.class) {
                    checkType(field, ClassSet.class);

                    ConfigClassSet annotation = (ConfigClassSet) declaredAnnotation;
                    String name = annotation.name().isEmpty() ? field.getName() : annotation.name();

                    ClassSet<?> classSet = (ClassSet<?>) field.get(null);
                    Objects.requireNonNull(classSet, field + " value must not be null");
                    Set<String> values = readStringCollection(cfg, name, annotation.category(), annotation.comment(), new HashSet<>(classSet.getRaw()));
                    classSet.clear();
                    classSet.addRaw(values);
                } else if (annotationType == ConfigEnum.class) {
                    checkType(field, Enum.class);
                    checkNotFinal(field);

                    ConfigEnum annotation = (ConfigEnum) declaredAnnotation;
                    String name = annotation.name().isEmpty() ? field.getName() : annotation.name();

                    Enum<?> defaultValue = (Enum<?>) field.get(null);
                    Objects.requireNonNull(defaultValue, field + " value must not be null");
                    String valueName = cfg.getString(name, annotation.category(), defaultValue.name(), annotation.comment());
                    try {
                        Enum<?> value = Enum.valueOf(defaultValue.getDeclaringClass(), valueName);
                        field.set(null, value);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                } else if (annotationType == ConfigStringCollection.class) {
                    // TODO Check generic type
                    checkType(field, Collection.class);

                    ConfigStringCollection annotation = (ConfigStringCollection) declaredAnnotation;
                    String name = annotation.name().isEmpty() ? field.getName() : annotation.name();

                    Collection<String> collection = (Collection<String>) field.get(null);
                    Objects.requireNonNull(collection, field + " value must not be null");
                    //readStringCollection(cfg, name, annotation.category(), annotation.comment(), collection);
                }
            }
        }
    }

    private static void invokeConfigLoadCallback(@NotNull Configuration cfg, @NotNull Method method) throws IllegalAccessException, InvocationTargetException {
        if (Modifier.isStatic(method.getModifiers()) && method.isAnnotationPresent(ConfigLoadCallback.class)) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length > 1)
                throw new IllegalArgumentException("Method " + method + " has @ConfigLoadCallback annotation, but requires " + parameterTypes.length + " arguments. Config load callback methods must require no more than one argument");
            if (parameterTypes.length == 1 && parameterTypes[0] != Configuration.class)
                throw new IllegalArgumentException("Method " + method + " has @ConfigLoadCallback annotation, but requires " + parameterTypes[0].getName() + " argument. Config load callback methods must require only Configuration argument");

            Object[] args = parameterTypes.length == 0 ? new Object[0] : new Object[] { cfg };
            method.setAccessible(true);
            method.invoke(null, args);
        }
    }

    private static void checkType(@NotNull Field field, @NotNull Class<?> expectedType) {
        Class<?> type = field.getType();
        Preconditions.checkArgument(expectedType == type || expectedType.isAssignableFrom(type), field + " type must be " + expectedType + " ( real type is " + type + ')');
    }

    private static void checkNotFinal(@NotNull Field field) {
        int modifiers = field.getModifiers();
        Preconditions.checkArgument(!Modifier.isFinal(modifiers), field + " must not be final");
    }

    @NotNull
    private static String getPackageName(@Nullable String className) {
        if (Strings.isNullOrEmpty(className))
            return PACKAGE_DEFAULT;
        int classDelimeterIndex = className.lastIndexOf('.');
        if (classDelimeterIndex == -1)
            return PACKAGE_DEFAULT;
        String packageName = className.substring(0, classDelimeterIndex);
        if (Strings.isNullOrEmpty(packageName))
            return PACKAGE_DEFAULT;
        int packageDelimeterIndex = packageName.lastIndexOf('.');
        if (packageDelimeterIndex == -1)
            return packageName;
        String simplePackageName = packageName.substring(packageDelimeterIndex + 1);
        return Strings.isNullOrEmpty(simplePackageName) ? PACKAGE_DEFAULT : simplePackageName;
    }

     */
}
