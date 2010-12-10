package eu.iksproject.fise.engines.salsadev;

import eu.iksproject.fise.engines.salsadev.core.SalsadevApiException;
import eu.iksproject.fise.servicesapi.ContentItem;
import eu.iksproject.fise.servicesapi.EngineException;
import eu.iksproject.fise.servicesapi.ServiceProperties;

/**
 * {@link SalsadevIndexingEnhancementEngine} push content for semantic indexing
 * on the Salsadev server for being able to later find related content using
 * another engine responsible for annotating content items with related document
 * links.
 *
 * Disabled: need to implement client id configuration first to avoid pushing
 * document references in public index of the Salsadev API: once this is done we
 * should publish a new enhancement engine to find related content using the
 * specific client id.
 */
// @Component(immediate = true, metatype = true)
// @Service
public class SalsadevIndexingEnhancementEngine extends
        AbstractSalsadevEnhancementEngine {

    /**
     * The default value for the Execution of this Engine. Currently set to
     * {@link ServiceProperties#ORDERING_PRE_PROCESSING}
     */
    public static final Integer DEFAULT_ORDER = ServiceProperties.ORDERING_PRE_PROCESSING;

    /**
     * {@inheritDoc}
     */
    @Override
    public void computeEnhancements(ContentItem ci) throws EngineException {
        try {
            apiProvider.index(ci.getStream(), ci.getMimeType(), ci.getId());
        } catch (SalsadevApiException e) {
            throw new EngineException(e.getLocalizedMessage(), e);
        }
    }

}
