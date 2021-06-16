package io.github.crucible.omniconfig.annotation.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import io.github.crucible.omniconfig.annotation.annotations.AnnotationConfig;
import io.github.crucible.omniconfig.annotation.annotations.values.ConfigBoolean;
import io.github.crucible.omniconfig.annotation.annotations.values.ConfigClassSet;
import io.github.crucible.omniconfig.annotation.annotations.values.ConfigDouble;
import io.github.crucible.omniconfig.annotation.annotations.values.ConfigEnum;
import io.github.crucible.omniconfig.annotation.annotations.values.ConfigFloat;
import io.github.crucible.omniconfig.annotation.annotations.values.ConfigInt;
import io.github.crucible.omniconfig.annotation.annotations.values.ConfigString;
import io.github.crucible.omniconfig.annotation.annotations.values.ConfigStringCollection;
import io.github.crucible.omniconfig.annotation.lib.ClassSet;
import io.github.crucible.omniconfig.core.Configuration;
import io.github.crucible.omniconfig.lib.Version;
import io.github.crucible.omniconfig.wrappers.Omniconfig;
import io.github.crucible.omniconfig.wrappers.values.EnumParameter;

public class AnnotationConfigReader {
    private static final String PACKAGE_DEFAULT = "default";
    private final Class<?> configClass;
    private final Map<Field, Annotation> annotatedFields = new HashMap<>();

    public AnnotationConfigReader(Class<?> configClass) {
        this.configClass = configClass;
    }

    public Omniconfig read() {
        AnnotationConfig annotation = this.configClass.getAnnotation(AnnotationConfig.class);
        Objects.requireNonNull(annotation, "Annotaion " + AnnotationConfig.class.getName() + " not found for class " + this.configClass.getName());

        String cfgName = annotation.name();
        if (Strings.isNullOrEmpty(cfgName)) {
            cfgName = this.configClass.getSimpleName();
        }

        this.parseAnnotations();

        Omniconfig.Builder wrapper = Omniconfig.builder(cfgName, new Version(annotation.version()), annotation.caseSensitiveCategories(), annotation.sidedType());
        wrapper.versioningPolicy(annotation.versioningPolicy());
        wrapper.terminateNonInvokedKeys(annotation.terminateNonInvokedKeys());

        wrapper.loadFile();

        this.loadFieldValues(wrapper);

        if (annotation.reloadable()) {
            wrapper.setReloadable();
        }

        return wrapper.build();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected void loadFieldValues(Omniconfig.Builder wrapper) {
        for (Entry<Field, Annotation> entry : this.annotatedFields.entrySet()) {
            Field field = entry.getKey();
            Annotation annotation = entry.getValue();
            Class<? extends Annotation> type = annotation.annotationType();

            if (type == ConfigBoolean.class) {
                this.checkType(field, boolean.class);
                this.checkFinal(field);

                ConfigBoolean configAnnotation = (ConfigBoolean) annotation;
                String name = configAnnotation.name().isEmpty() ? field.getName() : configAnnotation.name();

                boolean defaultValue = this.tryGetBoolean(field);

                this.forceCategories(wrapper, configAnnotation.category())
                .getBoolean(name, defaultValue)
                .comment(configAnnotation.comment())
                .uponLoad(value -> this.trySetBoolean(field, value.getValue()))
                .build();
            } else if (type == ConfigFloat.class) {
                this.checkType(field, float.class);
                this.checkFinal(field);

                ConfigFloat configAnnotation = (ConfigFloat) annotation;
                String name = configAnnotation.name().isEmpty() ? field.getName() : configAnnotation.name();

                float defaultValue = this.tryGetFloat(field);

                this.forceCategories(wrapper, configAnnotation.category())
                .getFloat(name, defaultValue)
                .comment(configAnnotation.comment())
                .min(configAnnotation.min()).max(configAnnotation.max())
                .uponLoad(value -> this.trySetFloat(field, value.getValue()))
                .build();
            } else if (type == ConfigDouble.class) {
                this.checkType(field, double.class);
                this.checkFinal(field);

                ConfigDouble configAnnotation = (ConfigDouble) annotation;
                String name = configAnnotation.name().isEmpty() ? field.getName() : configAnnotation.name();

                double defaultValue = this.tryGetDouble(field);

                this.forceCategories(wrapper, configAnnotation.category())
                .getDouble(name, defaultValue)
                .comment(configAnnotation.comment())
                .min(configAnnotation.min()).max(configAnnotation.max())
                .uponLoad(value -> this.trySetDouble(field, value.getValue()))
                .build();
            } else if (type == ConfigInt.class) {
                this.checkType(field, int.class);
                this.checkFinal(field);

                ConfigInt configAnnotation = (ConfigInt) annotation;
                String name = configAnnotation.name().isEmpty() ? field.getName() : configAnnotation.name();

                int defaultValue = this.tryGetInt(field);

                this.forceCategories(wrapper, configAnnotation.category())
                .getInteger(name, defaultValue)
                .comment(configAnnotation.comment())
                .min(configAnnotation.min()).max(configAnnotation.max())
                .uponLoad(value -> this.trySetInt(field, value.getValue()))
                .build();
            } else if (type == ConfigString.class) {
                this.checkType(field, String.class);
                this.checkFinal(field);

                ConfigString configAnnotation = (ConfigString) annotation;
                String name = configAnnotation.name().isEmpty() ? field.getName() : configAnnotation.name();

                String defaultValue = this.tryGetString(field);

                this.forceCategories(wrapper, configAnnotation.category())
                .getString(name, defaultValue)
                .comment(configAnnotation.comment())
                .uponLoad(value -> this.trySetString(field, value.getValue()))
                .build();
            } else if (type == ConfigClassSet.class) {
                this.checkType(field, ClassSet.class);

                ConfigClassSet configAnnotation = (ConfigClassSet) annotation;
                String name = configAnnotation.name().isEmpty() ? field.getName() : configAnnotation.name();

                ClassSet<?> classSet = (ClassSet<?>) this.tryGetValue(field);
                Objects.requireNonNull(classSet, field + " value must not be null");

                this.forceCategories(wrapper, configAnnotation.category())
                .getStringArray(name, classSet.getRaw().toArray(new String[0]))
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
                String name = configAnnotation.name().isEmpty() ? field.getName() : configAnnotation.name();

                Enum defaultValue = (Enum) this.tryGetValue(field);
                Objects.requireNonNull(defaultValue, field + " value must not be null");

                this.forceCategories(wrapper, configAnnotation.category())
                .getEnum(name, defaultValue)
                .comment(configAnnotation.comment())
                .uponLoad(value -> this.trySetValue(field, ((EnumParameter)value).getValue()))
                .build();
            } else if (type == ConfigStringCollection.class) {
                this.checkType(field, Collection.class);

                ConfigStringCollection configAnnotation = (ConfigStringCollection) annotation;
                String name = configAnnotation.name().isEmpty() ? field.getName() : configAnnotation.name();

                Collection<String> collection = (Collection<String>) this.tryGetValue(field);
                Objects.requireNonNull(collection, field + " value must not be null");

                this.forceCategories(wrapper, configAnnotation.category())
                .getStringArray(name, collection.toArray(new String[0]))
                .comment(configAnnotation.comment())
                .uponLoad(value -> {
                    collection.clear();

                    for (String string : value.getValue()) {
                        collection.add(string);
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
                            throw new IllegalArgumentException("Field " + field.getName() + "in annotation config " + this.configClass + "has more than one @Config[Value] annotation!");
                    }
                }
            }
        }
    }

    private Omniconfig.Builder forceCategories(Omniconfig.Builder builder, String categories) {
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
