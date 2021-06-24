package io.github.crucible.grimoire.common.test;

import java.util.Arrays;

import io.github.crucible.grimoire.common.core.ConfigBuildingManager;
import io.github.crucible.grimoire.common.core.MixinConfigBuilder;
import io.github.crucible.omniconfig.api.OmniconfigAPI;
import io.github.crucible.omniconfig.api.builders.IOmniconfigBuilder;
import io.github.crucible.omniconfig.api.core.SidedConfigType;
import io.github.crucible.omniconfig.api.core.VersioningPolicy;
import io.github.crucible.omniconfig.api.lib.Version;

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

        IOmniconfigBuilder wrapper = OmniconfigAPI.configBuilder("testdir/omnitest.cfg", new Version(version), SidedConfigType.COMMON);

        wrapper.versioningPolicy(VersioningPolicy.NOBLE);
        wrapper.terminateNonInvokedKeys(true);

        wrapper.loadFile();
        wrapper.pushCategory("Generic Config", "Just some generic stuff");

        wrapper.getInteger("exampleInt", exampleInt).comment("lol").minMax(100).sync()
        .uponLoad((value) -> {exampleInt = value.getValue(); System.out.println("Updated int: " + exampleInt);})
        .build();

        wrapper.getDouble("exampleDouble", exampleDouble).comment("example double or smth").min(-1).max(120000)
        .uponLoad((value) -> exampleDouble = value.getValue())
        .build();

        wrapper.pushCategory("Subcatt Thing", "Probably subcategory thing");

        wrapper.getBoolean("exampleBoolean", exampleBoolean).comment("aaaaaand example boolean thing").sync()
        .uponLoad((value) -> exampleBoolean = value.getValue())
        .build();

        wrapper.getString("exampleString", exampleString).comment("string kekw").sync()
        .validValues("LOLOLOLOL", "KEKEKEKEKEKE", "OMEGALUL")
        .uponLoad((value) -> exampleString = value.getValue())
        .build();

        wrapper.getStringList("exampleStringArray", exampleStringArray).comment("some string array").sync()
        .uponLoad((value) -> {exampleStringArray = value.getValueAsArray(); System.out.println("Array now: " + Arrays.asList(exampleStringArray));})
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
