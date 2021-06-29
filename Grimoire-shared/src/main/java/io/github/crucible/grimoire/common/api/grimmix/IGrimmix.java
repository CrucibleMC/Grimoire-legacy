package io.github.crucible.grimoire.common.api.grimmix;

import java.io.File;
import java.util.List;

import io.github.crucible.grimoire.common.api.grimmix.lifecycle.LoadingStage;
import io.github.crucible.grimoire.common.api.mixin.IMixinConfiguration;

/**
 * Serves as a representation of grimmix in common registry and
 * before other grimmixes. Be aware that this <b>must never be implemented
 * by controller itself</b>, as instead it is implemented by container
 * which Grimoire uses to operate with that controller.<br/><br/>
 *
 * Hereby, it serves as a possibility for grimmixes to retain some data
 * Grimoire have collected about themselves or about other grimmixes out
 * there.
 *
 * @author Aizistral
 */

public interface IGrimmix {

    /**
     * @return {@link String}-form ID used to distinguish this
     * grimmix among others, as specified by {@link Grimmix#id()}.
     */
    public String getID();

    /**
     * @return Textual name of this grimmix, as specified by
     * {@link Grimmix#name()}.
     */
    public String getName();

    /**
     * @return Priority this grimmix should have compared to other
     * grimmixes, as specified by {@link Grimmix#priority()}
     */
    public long getPriority();

    /**
     * @return Version of this grimmix, as specified by
     * {@link Grimmix#version()}
     */
    public String getVersion();

    /**
     * @return {@link LoadingStage} this grimmix currently goes
     * through.
     * @see LoadingStage
     */
    public LoadingStage getLoadingStage();

    /**
     * @return True if this grimmix is valid and fully capable
     * of receiving lifecycle events; false if it was internally
     * or externally invalidated at the time of
     * {@link LoadingStage#VALIDATION}.
     */
    public boolean isValid();

    /**
     * @return True if this grimmix is shipped as part of and
     * operated by Grimoire itself, false otherwise.
     */
    public boolean isGrimoireGrimmix();

    /**
     * @return List of valid {@link IMixinConfiguration} instances
     * that are registered and owned by this grimmix.
     * Unmodifiable.
     */
    public List<IMixinConfiguration> getOwnedConfigurations();

    /**
     * @return File that controller of this grimmix was discovered
     * within. This will normally be a .jar archive; however, in
     * development environment it can be one of the folders on
     * classpath.
     */
    public File getGrimmixFile();

}
