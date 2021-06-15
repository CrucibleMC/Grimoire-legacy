package io.github.crucible.grimoire.common.test;

import java.util.Arrays;

import io.github.crucible.omniconfig.OmniconfigCore;
import io.github.crucible.omniconfig.core.Configuration.SidedConfigType;
import io.github.crucible.omniconfig.core.Configuration.VersioningPolicy;
import io.github.crucible.omniconfig.wrappers.Omniconfig;

public class OmniconfigTest {

    public static int exampleInt = 23;
    public static double exampleDouble = 135.25;
    public static boolean exampleBoolean = false;
    public static String exampleString = "LOLOLOLOL";
    public static String[] exampleStringArray = { "LOL", "KEK", "MAN" };
    public static RandomEnum randomEnum = RandomEnum.VALUE_5;

    public static final OmniconfigTest INSTANCE = new OmniconfigTest();

    public OmniconfigTest() {
        String version = "1.8";

        Omniconfig.Builder wrapper = Omniconfig.builder("testdir" + OmniconfigCore.FILE_SEPARATOR + "omnitest", version, true, SidedConfigType.COMMON);

        wrapper.versioningPolicy(VersioningPolicy.NOBLE);
        wrapper.terminateNonInvokedKeys(true);

        wrapper.loadFile();
        wrapper.category("Generic Config", "Just some generic stuff");

        wrapper.getInteger("exampleInt", exampleInt).comment("lol").minMax(100).sync()
        .uponLoad((value) -> {exampleInt = value.getValue(); System.out.println("Updated int: " + exampleInt);})
        .build();

        wrapper.getDouble("exampleDouble", exampleDouble).comment("example double or smth").min(-1).max(120000)
        .uponLoad((value) -> exampleDouble = value.getValue())
        .build();

        wrapper.getBoolean("exampleBoolean", exampleBoolean).comment("aaaaaand example boolean thing").sync()
        .uponLoad((value) -> exampleBoolean = value.getValue())
        .build();

        wrapper.getString("exampleString", exampleString).comment("string kekw").sync()
        .validValues("LOLOLOLOL", "KEKEKEKEKEKE", "OMEGALUL")
        .uponLoad((value) -> exampleString = value.getValue())
        .build();

        wrapper.getStringArray("exampleStringArray", exampleStringArray).comment("some string array").sync()
        .uponLoad((value) -> {exampleStringArray = value.getArrayValue(); System.out.println("Array now: " + Arrays.asList(exampleStringArray));})
        .build();

        wrapper.getEnum("randomEnum", randomEnum).comment("Random enum bruh").sync()
        .validValues(RandomEnum.VALUE_1, RandomEnum.VALUE_3, RandomEnum.VALUE_5)
        .uponLoad((value) -> randomEnum = value.getValue())
        .build();

        wrapper.resetCategory();
        wrapper.setReloadable();

        wrapper.build();
    }

    public static enum RandomEnum {
        VALUE_1,
        VALUE_2,
        VALUE_3,
        VALUE_4,
        VALUE_5;
    }

}
