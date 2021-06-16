package io.github.crucible.grimoire.common.test;

import io.github.crucible.omniconfig.api.annotation.AnnotationConfig;
import io.github.crucible.omniconfig.api.annotation.properties.ConfigBoolean;
import io.github.crucible.omniconfig.api.annotation.properties.ConfigDouble;
import io.github.crucible.omniconfig.api.annotation.properties.ConfigEnum;
import io.github.crucible.omniconfig.api.annotation.properties.ConfigInt;
import io.github.crucible.omniconfig.api.annotation.properties.ConfigString;
import io.github.crucible.omniconfig.api.core.SidedConfigType;

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

    @ConfigEnum(comment = "Enum")
    public static SidedConfigType type = SidedConfigType.COMMON;

}
