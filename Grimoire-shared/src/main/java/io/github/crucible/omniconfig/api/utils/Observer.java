package io.github.crucible.omniconfig.api.utils;

import io.github.crucible.omniconfig.api.annotation.AnnotationConfig;

/**
 * In some cases it might be desired to make fields in {@link AnnotationConfig} class
 * visually final, so that only config proccessor itself will be able to modify their value.<br/>
 * While annotation config supports such possibility, Java compiler itself counteracts it by
 * replacing references to deterministic static final primitive fields with literal values of
 * those fields, making them unobservable at runtime.<br/><br/>
 *
 * This static class serves as a helper tool for surpassing that. Instead of assigning your fields
 * a value directry, you can call methods from this class, passing your primitive value into them
 * and getting exactly the same value back. While operation itself has no runtime effect, it will
 * convince the compiler to leave your fields observable.
 *
 * @author Aizistral
 */

public class Observer {

    /**
     * @param value Primitive <code>int</code> value;
     * @return Equals to passed value.
     */
    public static int retain(int value) {
        return value;
    }

    /**
     * @param value Primitive <code>double</code> value;
     * @return Equals to passed value.
     */
    public static double retain(double value) {
        return value;
    }

    /**
     * @param value Primitive <code>float</code> value;
     * @return Equals to passed value.
     */
    public static float retain(float value) {
        return value;
    }

    /**
     * @param value Primitive <code>long</code> value;
     * @return Equals to passed value.
     */
    public static long retain(long value) {
        return value;
    }

    /**
     * @param value Primitive <code>boolean</code> value;
     * @return Equals to passed value.
     */
    public static boolean retain(boolean value) {
        return value;
    }

    /**
     * @param value Primitive <code>short</code> value;
     * @return Equals to passed value.
     */
    public static short retain(short value) {
        return value;
    }

    /**
     * @param value Primitive <code>byte</code> value;
     * @return Equals to passed value.
     */
    public static byte retain(byte value) {
        return value;
    }

    /**
     * @param value {@link String} value;
     * @return Equals to passed value.
     */
    public static String retain(String value) {
        return value;
    }

    /**
     * @param value <code>{@link String}[]</code> value;
     * @return Equals to passed value.
     */
    public static String[] retain(String... value) {
        return value;
    }

    /**
     * @param value Enum value of given type;
     * @return Equals to passed value.
     */
    public static <V extends Enum<V>> V retain(V value) {
        return value;
    }

}
