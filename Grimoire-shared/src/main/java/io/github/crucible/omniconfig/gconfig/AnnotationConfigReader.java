package io.github.crucible.omniconfig.gconfig;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import io.github.crucible.omniconfig.api.OmniconfigAPI;
import io.github.crucible.omniconfig.api.annotation.AnnotationConfig;
import io.github.crucible.omniconfig.api.annotation.ConfigLoadCallback;
import io.github.crucible.omniconfig.api.annotation.ConfigLoadCallback.Stage;
import io.github.crucible.omniconfig.api.annotation.properties.ConfigBoolean;
import io.github.crucible.omniconfig.api.annotation.properties.ConfigClassSet;
import io.github.crucible.omniconfig.api.annotation.properties.ConfigDouble;
import io.github.crucible.omniconfig.api.annotation.properties.ConfigEnum;
import io.github.crucible.omniconfig.api.annotation.properties.ConfigFloat;
import io.github.crucible.omniconfig.api.annotation.properties.ConfigInt;
import io.github.crucible.omniconfig.api.annotation.properties.ConfigString;
import io.github.crucible.omniconfig.api.annotation.properties.ConfigStringCollection;
import io.github.crucible.omniconfig.api.builders.IOmniconfigBuilder;
import io.github.crucible.omniconfig.api.core.IOmniconfig;
import io.github.crucible.omniconfig.api.lib.ClassSet;
import io.github.crucible.omniconfig.api.lib.Version;
import io.github.crucible.omniconfig.backing.Configuration;
import io.github.crucible.omniconfig.core.properties.EnumParameter;

public class AnnotationConfigReader {
    private static final String PACKAGE_DEFAULT = "default";
    private final Class<?> configClass;
    private final Map<Field, Annotation> annotatedFields = new HashMap<>();
    private final Map<Method, Stage> loadingCallbacks = new HashMap<>();

    protected AnnotationConfigReader(Class<?> configClass) {
        this.configClass = configClass;
    }

    public IOmniconfig read() {
        AnnotationConfig annotation = this.configClass.getAnnotation(AnnotationConfig.class);
        Objects.requireNonNull(annotation, "Annotaion " + AnnotationConfig.class.getName() + " not found for class " + this.configClass.getName());

        String cfgName = annotation.name();
        if (Strings.isNullOrEmpty(cfgName)) {
            cfgName = this.configClass.getSimpleName();
        }

        this.parseAnnotations();

        IOmniconfigBuilder wrapper = OmniconfigAPI.configBuilder(cfgName, new Version(annotation.version()), annotation.sided());
        this.getLoadingCallbacks(Stage.BEFORE_INIT).stream().forEach(method -> this.tryInvoke(method, wrapper));

        wrapper.versioningPolicy(annotation.policy());
        wrapper.terminateNonInvokedKeys(annotation.terminateNonInvokedKeys());
        wrapper.loadFile();

        this.getLoadingCallbacks(Stage.AFTER_INIT).stream().forEach(method -> this.tryInvoke(method, wrapper));
        this.loadFieldValues(wrapper);
        this.getLoadingCallbacks(Stage.BEFORE_FINALIZATION).stream().forEach(method -> this.tryInvoke(method, wrapper));

        if (annotation.reloadable()) {
            wrapper.setReloadable();
        }

        return wrapper.build();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected void loadFieldValues(IOmniconfigBuilder wrapper) {
        for (Entry<Field, Annotation> entry : this.annotatedFields.entrySet()) {
            Field field = entry.getKey();
            Annotation annotation = entry.getValue();
            Class<? extends Annotation> type = annotation.annotationType();
            String rawName = field.getName();
            String fieldName = rawName.substring(0, 1).toUpperCase() + rawName.substring(1, rawName.length());

            if (type == ConfigBoolean.class) {
                this.checkType(field, boolean.class);
                this.checkFinal(field);

                ConfigBoolean configAnnotation = (ConfigBoolean) annotation;
                String name = configAnnotation.name().isEmpty() ? fieldName : configAnnotation.name();

                boolean defaultValue = this.tryGetBoolean(field);

                this.forceCategories(wrapper, configAnnotation.category())
                .getBoolean(name, defaultValue)
                .sync(configAnnotation.sync())
                .comment(configAnnotation.comment())
                .uponLoad(value -> this.trySetBoolean(field, value.getValue()))
                .build();
            } else if (type == ConfigFloat.class) {
                this.checkType(field, float.class);
                this.checkFinal(field);

                ConfigFloat configAnnotation = (ConfigFloat) annotation;
                String name = configAnnotation.name().isEmpty() ? fieldName : configAnnotation.name();

                float defaultValue = this.tryGetFloat(field);

                this.forceCategories(wrapper, configAnnotation.category())
                .getFloat(name, defaultValue)
                .sync(configAnnotation.sync())
                .comment(configAnnotation.comment())
                .min(configAnnotation.min()).max(configAnnotation.max())
                .uponLoad(value -> this.trySetFloat(field, value.getValue()))
                .build();
            } else if (type == ConfigDouble.class) {
                this.checkType(field, double.class);
                this.checkFinal(field);

                ConfigDouble configAnnotation = (ConfigDouble) annotation;
                String name = configAnnotation.name().isEmpty() ? fieldName : configAnnotation.name();

                double defaultValue = this.tryGetDouble(field);

                this.forceCategories(wrapper, configAnnotation.category())
                .getDouble(name, defaultValue)
                .sync(configAnnotation.sync())
                .comment(configAnnotation.comment())
                .min(configAnnotation.min()).max(configAnnotation.max())
                .uponLoad(value -> this.trySetDouble(field, value.getValue()))
                .build();
            } else if (type == ConfigInt.class) {
                this.checkType(field, int.class);
                this.checkFinal(field);

                ConfigInt configAnnotation = (ConfigInt) annotation;
                String name = configAnnotation.name().isEmpty() ? fieldName : configAnnotation.name();

                int defaultValue = this.tryGetInt(field);

                this.forceCategories(wrapper, configAnnotation.category())
                .getInteger(name, defaultValue)
                .sync(configAnnotation.sync())
                .comment(configAnnotation.comment())
                .min(configAnnotation.min()).max(configAnnotation.max())
                .uponLoad(value -> this.trySetInt(field, value.getValue()))
                .build();
            } else if (type == ConfigString.class) {
                this.checkType(field, String.class);
                this.checkFinal(field);

                ConfigString configAnnotation = (ConfigString) annotation;
                String name = configAnnotation.name().isEmpty() ? fieldName : configAnnotation.name();

                String defaultValue = this.tryGetString(field);

                this.forceCategories(wrapper, configAnnotation.category())
                .getString(name, defaultValue)
                .sync(configAnnotation.sync())
                .comment(configAnnotation.comment())
                .uponLoad(value -> this.trySetString(field, value.getValue()))
                .build();
            } else if (type == ConfigClassSet.class) {
                this.checkType(field, ClassSet.class);

                ConfigClassSet configAnnotation = (ConfigClassSet) annotation;
                String name = configAnnotation.name().isEmpty() ? fieldName : configAnnotation.name();

                ClassSet<?> classSet = (ClassSet<?>) this.tryGetValue(field);
                Objects.requireNonNull(classSet, field + " value must not be null");

                this.forceCategories(wrapper, configAnnotation.category())
                .getStringList(name, classSet.getRaw().toArray(new String[0]))
                .sync(configAnnotation.sync())
                .comment(configAnnotation.comment())
                .uponLoad(value ->  {
                    classSet.clear();
                    classSet.addRaw(value.getValue());
                })
                .build();
            } else if (type == ConfigEnum.class) {
                this.checkType(field, Enum.class);
                this.checkFinal(field);

                ConfigEnum configAnnotation = (ConfigEnum) annotation;
                String name = configAnnotation.name().isEmpty() ? fieldName : configAnnotation.name();

                Enum defaultValue = (Enum) this.tryGetValue(field);
                Objects.requireNonNull(defaultValue, field + " value must not be null");

                this.forceCategories(wrapper, configAnnotation.category())
                .getEnum(name, defaultValue)
                .sync(configAnnotation.sync())
                .comment(configAnnotation.comment())
                .uponLoad(value -> this.trySetValue(field, ((EnumParameter)value).getValue()))
                .build();
            } else if (type == ConfigStringCollection.class) {
                this.checkType(field, Collection.class);

                ConfigStringCollection configAnnotation = (ConfigStringCollection) annotation;
                String name = configAnnotation.name().isEmpty() ? fieldName : configAnnotation.name();

                Collection<String> collection = (Collection<String>) this.tryGetValue(field);
                Objects.requireNonNull(collection, field + " value must not be null");

                this.forceCategories(wrapper, configAnnotation.category())
                .getStringList(name, collection.toArray(new String[0]))
                .sync(configAnnotation.sync())
                .comment(configAnnotation.comment())
                .uponLoad(value -> {
                    try {
                        collection.clear();

                        for (String string : value.getValue()) {
                            collection.add(string);
                        }
                    } catch (UnsupportedOperationException ex) {
                        throw new IllegalStateException("List \"" + rawName + "\" annoted with @ConfigStringCollection in class " + this.configClass + " does not support modification operations!", ex);
                    }
                })
                .build();
            }

        }
    }

    protected void parseAnnotations() {
        for (Field field : this.configClass.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                field.setAccessible(true);

                for (Annotation declaredAnnotation : field.getDeclaredAnnotations()) {
                    Class<? extends Annotation> type = declaredAnnotation.annotationType();

                    if (type == ConfigBoolean.class || type == ConfigClassSet.class || type == ConfigEnum.class ||
                            type == ConfigFloat.class || type == ConfigInt.class || type == ConfigString.class ||
                            type == ConfigStringCollection.class || type == ConfigDouble.class) {

                        if (!this.annotatedFields.containsKey(field)) {
                            this.annotatedFields.put(field, declaredAnnotation);
                        } else
                            throw new IllegalArgumentException("Field " + field.getName() + " in annotation config " + this.configClass + " has more than one @Config[Value] annotation!");
                    }
                }
            }
        }

        for (Method method : this.configClass.getDeclaredMethods()) {
            if (Modifier.isStatic(method.getModifiers())) {
                method.setAccessible(true);

                for (Annotation declaredAnnotation : method.getDeclaredAnnotations()) {
                    Class<? extends Annotation> type = declaredAnnotation.annotationType();

                    if (type == ConfigLoadCallback.class) {
                        if (method.getParameterCount() == 1 && method.getParameterTypes()[0] == IOmniconfigBuilder.class) {
                            ConfigLoadCallback annotation = (ConfigLoadCallback) declaredAnnotation;
                            this.loadingCallbacks.put(method, annotation.value());
                        } else
                            throw new IllegalArgumentException("Method " + method.getName() + " in annotation config " + this.configClass
                                    + " is annotated with @ConfigLoadCallback and thus must accept single IOmniconfigBuilder argument, but does not!");
                    }
                }
            }
        }
    }

    private void tryInvoke(Method method, IOmniconfigBuilder builder) {
        try {
            method.invoke(null, builder);
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    private List<Method> getLoadingCallbacks(Stage stage) {
        return this.loadingCallbacks.entrySet().stream().filter(entry -> entry.getValue() == stage)
                .collect(ArrayList::new, (list, entry) -> list.add(entry.getKey()), ArrayList::addAll);
    }

    private IOmniconfigBuilder forceCategories(IOmniconfigBuilder builder, String categories) {
        String[] array;
        builder.resetCategory();

        if (categories.contains(Configuration.CATEGORY_SPLITTER)) {
            array = categories.split("\\" + Configuration.CATEGORY_SPLITTER);
        } else {
            array = new String[] { categories };
        }

        for (String str : array) {
            builder.pushCategory(str);
        }

        return builder;
    }

    private void checkType(@NotNull Field field, @NotNull Class<?> expectedType) {
        Class<?> type = field.getType();
        Preconditions.checkArgument(expectedType == type || expectedType.isAssignableFrom(type), field + " type must be " + expectedType + " ( real type is " + type + ')');
    }

    private void checkFinal(@NotNull Field field) {
        int modifiers = field.getModifiers();

        if (Modifier.isFinal(modifiers)) {
            try {
                Field modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @NotNull
    private String getPackageName(@Nullable String className) {
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

    // REFLECTION HELPER METHODS //

    protected void trySetValue(Field field, Object value) {
        try {
            field.set(null, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected Object tryGetValue(Field field) {
        try {
            return field.get(null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    protected void trySetString(Field field, String String) {
        try {
            field.set(null, String);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected String tryGetString(Field field) {
        try {
            return (String) field.get(null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    protected void trySetBoolean(Field field, boolean Boolean) {
        try {
            field.set(null, Boolean);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected boolean tryGetBoolean(Field field) {
        try {
            return field.getBoolean(null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    protected void trySetFloat(Field field, float Float) {
        try {
            field.set(null, Float);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected float tryGetFloat(Field field) {
        try {
            return field.getFloat(null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    protected void trySetDouble(Field field, double Double) {
        try {
            field.set(null, Double);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected double tryGetDouble(Field field) {
        try {
            return field.getDouble(null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    protected void trySetInt(Field field, int Int) {
        try {
            field.set(null, Int);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected int tryGetInt(Field field) {
        try {
            return field.getInt(null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

}
