package io.github.crucible.omniconfig.api.core;

// TODO Document two other policies, now that they actually work
public enum VersioningPolicy {
    /**
     * This policy enforces full reset of config file if version does not match.
     * Contents of the existing file will be entirely disregarded when loading,
     * therefore all values will fall back to their defaults when saving, and any
     * unneccessary data will be cleaned.
     */
    AGGRESSIVE,
    /**
     * [NYI]
     */
    RESPECTFUL,
    /**
     * [NYI]
     */
    NOBLE,
    /**
     * With this policy (used by default) config implementation will not take any
     * action if config versions do not match. It will simply update config version
     * to defined one upon saving the file, and leave anything else as is.
     */
    DISMISSIVE;
}