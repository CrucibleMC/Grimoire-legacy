package io.github.crucible.omniconfig.api.utils;

public class Observer {

    public static int retain(int value) {
        return value;
    }

    public static double retain(double value) {
        return value;
    }

    public static float retain(float value) {
        return value;
    }

    public static long retain(long value) {
        return value;
    }

    public static boolean retain(boolean value) {
        return value;
    }

    public static short retain(short value) {
        return value;
    }

    public static byte retain(byte value) {
        return value;
    }

    public static String retain(String value) {
        return value;
    }

    public static String[] retain(String... value) {
        return value;
    }

    public static <V extends Enum<V>> V retain(V value) {
        return value;
    }

}
