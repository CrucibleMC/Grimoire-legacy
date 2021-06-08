package io.github.crucible.omniconfig.wrappers;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import io.github.crucible.omniconfig.OmniconfigCore;
import io.github.crucible.omniconfig.core.Configuration;
import io.github.crucible.omniconfig.core.SynchronizationManager;
import io.github.crucible.omniconfig.packets.PacketSyncOptions;
import io.github.crucible.omniconfig.wrappers.values.BooleanParameter;
import io.github.crucible.omniconfig.wrappers.values.DoubleParameter;
import io.github.crucible.omniconfig.wrappers.values.EnumParameter;
import io.github.crucible.omniconfig.wrappers.values.AbstractParameter;
import io.github.crucible.omniconfig.wrappers.values.IntegerParameter;
import io.github.crucible.omniconfig.wrappers.values.PerhapsParameter;
import io.github.crucible.omniconfig.wrappers.values.StringArrayParameter;
import io.github.crucible.omniconfig.wrappers.values.StringParameter;

public class OmniconfigWrapper {
    public static final Map<String, OmniconfigWrapper> wrapperRegistry = new HashMap<>();

    public final Configuration config;
    private String currentCategory;
    private String currentComment;
    private double currentMin;
    private double currentMax;
    private boolean currentSynchronized;
    public final Map<String, AbstractParameter<?>> invokationMap;
    private boolean deferInvokation;
    private boolean forceSynchronized;
    private boolean currentClientOnly;
    private String optionPrefix;
    private double defaultMin;
    private double defaultMax;

    private OmniconfigWrapper(Configuration config) {
        this.config = config;

        this.defaultMin = 0;
        this.defaultMax = OmniconfigCore.STANDART_INTEGER_LIMIT;

        this.invokationMap = new HashMap<>();
        this.deferInvokation = false;
        this.forceSynchronized = false;

        this.resetCategory();
        this.resetOptionPrefix();
        this.resetOptionStuff();

        wrapperRegistry.put(this.config.getConfigFile().getName(), this);
    }

    public static OmniconfigWrapper setupBuilder(String fileName) {
        return setupBuilder(fileName, null);
    }

    public static OmniconfigWrapper setupBuilder(String fileName, String version) {
        return setupBuilder(fileName, false, version);
    }

    public static OmniconfigWrapper setupBuilder(String fileName, boolean caseSensitive, String version) {
        try {
            return setupBuilder(new File(OmniconfigCore.CONFIG_DIR, fileName+".omniconf"), caseSensitive, version);
        } catch (Exception ex) {
            new RuntimeException("Something screwed up when loading config.", ex).printStackTrace();
            return null;
        }
    }

    public static OmniconfigWrapper setupBuilder(File file) {
        return setupBuilder(file, null);
    }

    public static OmniconfigWrapper setupBuilder(File file, String version) {
        return setupBuilder(file, false, version);
    }

    public static OmniconfigWrapper setupBuilder(File file, boolean caseSensitive, String version) {
        return new OmniconfigWrapper(new Configuration(file, version, caseSensitive));
    }

    public static OmniconfigWrapper setupBuilder(Configuration config) {
        return new OmniconfigWrapper(config);
    }

    private void resetOptionStuff() {
        this.resetComment();
        this.resetCurrentMin();
        this.resetCurrentMax();
        this.resetCurrentSynchronized();
        this.resetClientOnly();
    }

    public void setDefaultMin(double defaultMin) {
        this.defaultMin = defaultMin;
        this.resetCurrentMin();
    }

    public void setDefaultMax(double defaultMax) {
        this.defaultMax = defaultMax;
        this.resetCurrentMax();
    }

    private void resetOptionPrefix() {
        this.optionPrefix = "";
    }

    private void resetCurrentSynchronized() {
        this.currentSynchronized = false || this.forceSynchronized;
    }

    private void resetClientOnly() {
        this.currentClientOnly = false;
    }

    private void resetCurrentMin() {
        this.currentMin = this.defaultMin;
    }

    private void resetCurrentMax() {
        this.currentMax = this.defaultMax;
    }

    private void resetComment() {
        this.currentComment = "Undocumented property";
    }

    private void resetCategory() {
        this.currentCategory = Configuration.CATEGORY_GENERAL;
    }

    public void deferInvocation(boolean whetherOrNot) {
        this.deferInvokation = whetherOrNot;
    }

    public void forceSynchronized(boolean whetherOrNot) {
        this.forceSynchronized = whetherOrNot;
        this.resetCurrentSynchronized();
    }

    public boolean isClientOnly() {
        return this.currentClientOnly;
    }

    // TODO Make this happen per-file rather than per-option
    public OmniconfigWrapper clientOnly() {
        return this.clientOnly(true);
    }

    public OmniconfigWrapper clientOnly(boolean clientOnly) {
        this.currentClientOnly = clientOnly;
        return this;
    }

    public boolean isForceSynchronized() {
        return this.forceSynchronized;
    }

    public boolean isInvokationDeferred() {
        return this.deferInvokation;
    }

    public OmniconfigWrapper setReloadable() {
        this.pushGenericOverloadingAction();
        this.pushBeholderAttachment();

        return this;
    }

    public String getCurrentCategory() {
        return this.currentCategory;
    }

    public OmniconfigWrapper pushOverloadingAction(Consumer<Configuration> action) {
        this.config.attachOverloadingAction(action);
        return this;
    }

    /**
     * Generic action automatically re-invokes all extensions of {@link AbstractParameter}
     * that were invoked during initial loading procedure. Since references you retain from builder
     * point to those objects, values you get from them are automatically updated.<br/><br/>
     *
     * Values pulled from config file are corrected, but corrections aren't saved back to file.
     * You can use {@link #pushOverloadingAction(Consumer)} to attach your own action if you are sure
     * you know what you're doing..<br/><br/>
     *
     * Values that are marked as synchronizable will not be invoked when we are on remote server,
     * since they are supposed to be received from server instead. If we are indeed the remote server
     * itself, then we will dispatch those values to all remote clients through packet.
     */

    public OmniconfigWrapper pushGenericOverloadingAction() {
        this.pushOverloadingAction(config -> {
            config.load();
            if (this.invokationMap != null) {
                this.invokationMap.values().forEach(param -> {
                    if (!OmniconfigCore.onRemoteServer || !param.isSynchronized()) {
                        param.invoke(config);
                    }
                });
            }

            SynchronizationManager.syncToAll(this);
        });
        return this;
    }

    public OmniconfigWrapper pushBeholderAttachment() {
        this.config.attachBeholder();
        return this;
    }

    public OmniconfigWrapper pushPrefix(String optionPrefix) {
        this.optionPrefix = optionPrefix;
        return this;
    }

    public OmniconfigWrapper popPrefix() {
        this.resetOptionPrefix();
        return this;
    }

    public OmniconfigWrapper pushTerminateNonInvokedKeys(boolean whetherOrNot) {
        this.config.setTerminateNonInvokedKeys(whetherOrNot);
        return this;
    }

    public OmniconfigWrapper pushVersioningPolicy(Configuration.VersioningPolicy policy) {
        this.config.setVersioningPolicy(policy);
        return this;
    }

    public OmniconfigWrapper pushSidedType(Configuration.SidedConfigType type) {
        this.config.setSidedType(type);
        return this;
    }

    public OmniconfigWrapper loadConfigFile() {
        this.config.load();
        return this;
    }

    public OmniconfigWrapper pushCategory(String name) {
        this.currentCategory = name;
        return this;
    }

    public OmniconfigWrapper pushCategory(String name, String comment) {
        this.pushCategory(name); // TODO Proper subcategories
        this.config.addCustomCategoryComment(name, comment);
        return this;
    }

    public OmniconfigWrapper popCategory() {
        this.currentCategory = Configuration.CATEGORY_GENERAL;
        return this;
    }

    public OmniconfigWrapper comment(String comment) {
        this.currentComment = comment;
        return this;
    }

    public OmniconfigWrapper min(double minValue) {
        this.currentMin = minValue;
        return this;
    }

    public OmniconfigWrapper max(double maxValue) {
        this.currentMax = maxValue;
        return this;
    }

    public OmniconfigWrapper minMax(double value) {
        this.max(value);
        this.min(-value);
        return this;
    }

    public OmniconfigWrapper sync() {
        return this.sync(true);
    }

    public OmniconfigWrapper nosync() {
        return this.sync(false);
    }

    public OmniconfigWrapper sync(boolean whetherOrNot) {
        this.currentSynchronized = whetherOrNot;
        return this;
    }

    public StringParameter getString(String name, String defaultValue) {
        return this.getString(name, defaultValue, (String[])null);
    }

    public StringArrayParameter getStringArray(String name, String[] defaultValue) {
        return this.getStringArray(name, defaultValue, (String[])null);
    }

    public <V extends Enum<V>> EnumParameter<V> getEnum(String name, V defaultValue) {
        return this.getEnum(name, defaultValue, (V[])null);
    }

    @SuppressWarnings("unchecked")
    public <V extends Enum<V>> EnumParameter<V> getEnum(String name, V defaultValue, V... validValues) {
        EnumParameter<V> returned = new EnumParameter<>(defaultValue);
        returned.setName(this.optionPrefix + name);
        returned.setCategory(this.currentCategory);
        returned.setComment(this.currentComment);
        returned.setSynchronized(this.currentSynchronized);
        returned.setClientOnly(this.currentClientOnly);

        if (validValues != null && validValues.length > 0) {
            returned.setValidValues(validValues);
        }

        this.resetOptionStuff();

        this.invokationMap.put(returned.getId(), returned);
        if (!this.deferInvokation) {
            returned.invoke(this.config);
        }

        return returned;
    }

    public StringArrayParameter getStringArray(String name, String[] defaultValue, String[] validValues) {
        StringArrayParameter returned = new StringArrayParameter(defaultValue);

        this.applyGenericProperties(returned, name);

        if (validValues != null && validValues.length > 0) {
            returned.setValidValues(validValues);
        }

        this.resetOptionStuff();

        this.invokationMap.put(returned.getId(), returned);
        if (!this.deferInvokation) {
            returned.invoke(this.config);
        }

        return returned;
    }

    public StringParameter getString(String name, String defaultValue, String... validValues) {
        StringParameter returned = new StringParameter(defaultValue);

        this.applyGenericProperties(returned, name);

        if (validValues != null && validValues.length > 0) {
            returned.setValidValues(validValues);
        }

        this.resetOptionStuff();

        this.invokationMap.put(returned.getId(), returned);
        if (!this.deferInvokation) {
            returned.invoke(this.config);
        }

        return returned;
    }

    public BooleanParameter getBoolean(String name, boolean defaultValue) {
        BooleanParameter returned = new BooleanParameter(defaultValue);

        this.applyGenericProperties(returned, name);
        this.resetOptionStuff();

        this.invokationMap.put(returned.getId(), returned);
        if (!this.deferInvokation) {
            returned.invoke(this.config);
        }

        return returned;
    }

    public IntegerParameter getInt(String name, int defaultValue) {
        IntegerParameter returned = new IntegerParameter(defaultValue);

        this.applyGenericProperties(returned, name);
        returned.setMinValue((int) this.currentMin);
        returned.setMaxValue((int) this.currentMax);

        this.resetOptionStuff();

        this.invokationMap.put(returned.getId(), returned);
        if (!this.deferInvokation) {
            returned.invoke(this.config);
        }

        return returned;
    }

    public DoubleParameter getDouble(String name, double defaultValue) {
        DoubleParameter returned = new DoubleParameter(defaultValue);

        this.applyGenericProperties(returned, name);
        returned.setMinValue(this.currentMin);
        returned.setMaxValue(this.currentMax);

        this.resetOptionStuff();

        this.invokationMap.put(returned.getId(), returned);
        if (!this.deferInvokation) {
            returned.invoke(this.config);
        }

        return returned;
    }

    public PerhapsParameter getPerhaps(String name, int defaultPercentage) {
        PerhapsParameter returned = new PerhapsParameter(defaultPercentage);

        this.applyGenericProperties(returned, name);
        returned.setMinValue((int) this.currentMin);
        returned.setMaxValue((int) this.currentMax);

        this.resetOptionStuff();

        this.invokationMap.put(returned.getId(), returned);
        if (!this.deferInvokation) {
            returned.invoke(this.config);
        }

        return returned;
    }

    protected void applyGenericProperties(AbstractParameter<?> param, String name) {
        param.setName(this.optionPrefix + name);
        param.setCategory(this.currentCategory);
        param.setComment(this.currentComment);
        param.setSynchronized(this.currentSynchronized);
        param.setClientOnly(this.currentClientOnly);
    }

    public Configuration build() {
        if (!this.deferInvokation) {
            this.config.save();
        }

        return this.config;
    }

    public Collection<AbstractParameter<?>> retrieveInvocationList() {
        return this.invokationMap.values();
    }

}
