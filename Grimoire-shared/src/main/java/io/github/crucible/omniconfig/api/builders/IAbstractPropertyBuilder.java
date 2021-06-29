package io.github.crucible.omniconfig.api.builders;

import java.util.function.Consumer;

import io.github.crucible.omniconfig.api.properties.IAbstractProperty;

/**
 * Generic interface that all specialized sub-property builders inherit from.<br><br>
 *
 * One thing that this interface does not reflect, but which is common for all
 * sub-property builders is a possibility to assign validator function to future property,
 * so it will be described below:<br><br>
 *
 * Validator function accepts bare property value immediately after it is parsed
 * from physical config file, and may perform checks to ensure that value satisfies
 * some arbitrary conditions. If it does, function should return same value it
 * received; otherwise it is free to return any sort of "closest valid" or "default"
 * value which will be assigned to property instance instead, and saved back to file if
 * this is the first time file is loaded from disc.<br>
 * This allows to assign complex validitation conditions to config properties, for
 * instance - to restrict a value property can take based on which value another property
 * has, and serves as an alternative to raising application-crashing exception with
 * error message informing the user why their configuration is invalid. However, you
 * should be aware that always using validators for that is not practical; if you
 * have complex properties, like sizeless string arrays that may contain non-trivial
 * user-created data and demand complex syntax, dropping their value every time something
 * is wrong with that syntax will probably not make your users happy. Realize nuances and
 * use this feature with care.
 *
 * @param <E> Property type this builder will create.
 * @param <T> Builder interface type itself.
 * @author Aizistral
 */

public abstract interface IAbstractPropertyBuilder<E extends IAbstractProperty, T extends IAbstractPropertyBuilder<E, T>> {

    /**
     * Add human-readable comment for this config property.
     *
     * @param comment The comment.
     * @return This sub-builder instance.
     */
    public T comment(String comment);

    /**
     * Mark this property as synchronized or non-synchronized, depending
     * on boolean value passed as argument.
     *
     * @param isSyncable True if property should be synchronized, false otherwise.
     * @return This sub-builder instance.
     */
    public T sync(boolean isSyncable);

    /**
     * Mark this property as synchronized.
     * Equivalent to call: <code>IAbstractPropertyBuilder#sync(true)</code>.
     *
     * @return This sub-builder instance.
     */
    public T sync();

    /**
     * Same as {@link #uponLoad(Consumer)}, but if second argument is false,
     * provided consumer will not be invoked when property is first built and
     * loaded, instead only being called after subsequent value updates,
     * for instance when reloading config file marked as reloadable via
     * {@link IOmniconfigBuilder#setReloadable()}, or when assigning property
     * values received from server for synchronized properties.
     *
     * @param consumer Consumer to invoke.
     * @param invokeOnFirstLoad Whether or not to invoke provided consumer
     * when property is loaded for the first time.
     * @return This sub-builder instance.
     */
    public T uponLoad(Consumer<E> consumer, boolean invokeOnFirstLoad);

    /**
     * Specify consumer which will be invoked every time a property value is
     * loaded from physical file, accepting property instance as argument.
     * This includes time when the property is first built, and thus may
     * come in handy if you are not interested in keeping reference to property
     * instance itself, and instead want it's bare value to be assigned to
     * some simple field in your config handler class which you could then
     * refer to. As en example, consider the following snippet:
     *
     * <pre>
     * private static int specialValue = 10;
     *
     * // ...more class fields or something
     *
     * public static void loadConfig() {
     *     // ... previous config logic
     *
     *     omniconfigBuilder.getInteger("specialInteger", specialValue)
     *     .comment("Incredibly very special integer value.")
     *     .uponLoad(property -> specialValue = property.getValue())
     *     .build();
     *
     *     // ... subsequent config logic
     * }
     * </pre>
     *
     * Such implementation will ensure that your <code>specialValue</code> will
     * be automatically assigned during initial config loading, as well as during
     * all subsequent re-loads if config was marked as reloadable via
     * {@link IOmniconfigBuilder#setReloadable()}. From here you should be able to
     * guess how that can be useful.
     *
     * @param consumer Consumer to invoke
     * @return This sub-builder instance.
     */
    public T uponLoad(Consumer<E> consumer);

    /**
     * Build appropriate property instance and thus finish this sub-builder's execution.
     * @return Property instance.
     */
    public E build();

}
