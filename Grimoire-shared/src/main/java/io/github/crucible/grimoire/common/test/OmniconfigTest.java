package io.github.crucible.grimoire.common.test;

import java.util.Arrays;

import io.github.crucible.omniconfig.core.Configuration.VersioningPolicy;
import io.github.crucible.omniconfig.wrappers.OmniconfigWrapper;

public class OmniconfigTest {

    public static int exampleInt = 23;
    public static double exampleDouble = 2.0;
    public static boolean exampleBoolean = false;
    public static String exampleString = "LOLOLOLOL";
    public static String[] exampleStringArray = { "LOL", "KEK", "MAN" };

    public static final OmniconfigTest INSTANCE = new OmniconfigTest();

    public OmniconfigTest() {
        OmniconfigWrapper wrapper = OmniconfigWrapper.setupBuilder("omnitest", true, "1.0");

        wrapper.pushVersioningPolicy(VersioningPolicy.AGGRESSIVE);
        wrapper.pushTerminateNonInvokedKeys(true);

        wrapper.loadConfigFile();
        wrapper.pushCategory("Generic Config", "Just some generic stuff");

        wrapper.comment("lol").minMax(100).getInt("exampleInt", exampleInt)
        .uponInvoke((value) -> {exampleInt = value.getValue(); System.out.println("Updated int: " + exampleInt);});

        wrapper.comment("example double or smth").min(-1).max(120000).getDouble("exampleDouble", exampleDouble)
        .uponInvoke((value) -> exampleDouble = value.getValue());

        wrapper.comment("aaaaaand example boolean thing").getBoolean("exampleBoolean", exampleBoolean)
        .uponInvoke((value) -> exampleBoolean = value.getValue());

        wrapper.comment("string kekw").getString("exampleString", exampleString, "LOLOLOLOL", "KEKEKEKEKEKE", "OMEGALUL")
        .uponInvoke((value) -> exampleString = value.getValue());

        wrapper.comment("some string array").getStringArray("exampleStringArray", exampleStringArray)
        .uponInvoke((value) -> {exampleStringArray = value.getValue(); System.out.println("Array now: " + Arrays.asList(exampleStringArray));});

        wrapper.popCategory();
        wrapper.build();

        wrapper.setReloadable();
    }

}
