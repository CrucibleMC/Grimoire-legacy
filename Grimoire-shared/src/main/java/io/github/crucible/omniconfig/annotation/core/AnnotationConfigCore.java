package io.github.crucible.omniconfig.annotation.core;

import java.util.ArrayList;
import java.util.List;

public class AnnotationConfigCore {
    public static final AnnotationConfigCore INSTANCE = new AnnotationConfigCore();
    public final List<Class<?>> configClassList = new ArrayList<>();

    public AnnotationConfigCore() {
        // NO-OP
    }

    public void addAnnotationConfig(Class<?> configClass) {
        if (!this.configClassList.contains(configClass)) {
            this.configClassList.add(configClass);

            AnnotationConfigReader reader = new AnnotationConfigReader(configClass);
            reader.read();
        } else
            throw new IllegalArgumentException("Annotation config class " + configClass + "was already registered!");
    }
}
