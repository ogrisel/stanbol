package eu.iksproject.fise.servicesapi;

import java.util.Map;

/**
 * FISE components might implement this interface to parse additional
 * properties to other components.
 *
 * @author Rupert Westenthaler
 */
public interface ServiceProperties {

    /**
     * Getter for the properties defined by this service.
     * @return An unmodifiable map of properties defined by this service
     */
    Map<String,Object> getServiceProperties();

    //TODO review the definition of constants
    /**
     * Property Key used to define the order in which {@link EnhancementEngine}s are
     * called by the {@link EnhancementJobManager}. This property expects a
     * single {@link Integer} as value
     */
    String ENHANCEMENT_ENGINE_ORDERING = "eu.iksproject.fise.engine.order";

    /**
     * Ordering values >= this value indicate, that an enhancement engine
     * dose some pre processing on the content
     */
    Integer ORDERING_PRE_PROCESSING = 200;

    /**
     * Ordering values < {@link ServiceProperties#ORDERING_PRE_PROCESSING} and
     * >= this value indicate, that an enhancement engine performs operations
     * that are only dependent on the parsed content.
     */
    Integer ORDERING_CONTENT_EXTRACTION = 100;

    /**
     * Ordering values < {@link ServiceProperties#ORDERING_CONTENT_EXTRACTION}
     * and >= this value indicate, that an enhancement engine performs operations
     * on extracted features of the content. It can also extract additional
     * enhancement by using the content, but such features might not be
     * available to other engines using this ordering range
     */
    Integer ORDERING_EXTRACTION_ENHANCEMENT = 1;

    /**
     * The default ordering uses {@link ServiceProperties#ORDERING_EXTRACTION_ENHANCEMENT}
     * -1 . So by default EnhancementEngines are called after all engines that
     * use an value within the ordering range defined by
     * {@link ServiceProperties#ORDERING_EXTRACTION_ENHANCEMENT}
     */
    Integer ORDERING_DEFAULT = 0;

    /**
     * Ordering values < {@link ServiceProperties#ORDERING_DEFAULT} and >= this
     * value indicate that an enhancement engine performs post processing
     * operations on existing enhancements.
     */
    Integer ORDERING_POST_PROCESSING = -100;

}
