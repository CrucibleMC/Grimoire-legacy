package io.github.crucible.grimoire.common.test;

import io.github.crucible.omniconfig.annotation.annotations.AnnotationConfig;
import io.github.crucible.omniconfig.annotation.annotations.values.*;

@AnnotationConfig
public class AnnotationConfigTest {

    @ConfigInt(min = 0, max = 1000, comment = "Some example int")
    public static int exampleInt = 23;

    @ConfigDouble(min = -1, max = 200.0, comment = "Some example double")
    public static double exampleDouble = 2.0;

    @ConfigBoolean(comment = "Boolean thing example")
    public static boolean exampleBoolean = false;

    @ConfigString(comment = "String thing")
    public static String exampleString = "LOLOLOLOL";

}
