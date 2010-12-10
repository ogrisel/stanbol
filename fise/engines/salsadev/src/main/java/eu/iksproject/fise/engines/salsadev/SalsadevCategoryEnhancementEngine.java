package eu.iksproject.fise.engines.salsadev;

import org.apache.clerezza.rdf.core.LiteralFactory;
import org.apache.clerezza.rdf.core.MGraph;
import org.apache.clerezza.rdf.core.UriRef;
import org.apache.clerezza.rdf.core.impl.TripleImpl;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;

import eu.iksproject.fise.engines.salsadev.core.SalsadevApiException;
import eu.iksproject.fise.engines.salsadev.core.SalsadevConstants;
import eu.iksproject.fise.engines.salsadev.core.xml.pojo.Category;
import eu.iksproject.fise.engines.salsadev.core.xml.pojo.CategoryList;
import eu.iksproject.fise.servicesapi.ContentItem;
import eu.iksproject.fise.servicesapi.EngineException;
import eu.iksproject.fise.servicesapi.EnhancementEngine;
import eu.iksproject.fise.servicesapi.helper.EnhancementEngineHelper;
import eu.iksproject.fise.servicesapi.rdf.Properties;

/**
 * Extract the main categories of a text document using the Salsadev public API.
 */
@Component(immediate = true, metatype = true)
@Service
public class SalsadevCategoryEnhancementEngine extends
        AbstractSalsadevEnhancementEngine implements EnhancementEngine {

    /**
     * {@inheritDoc}
     */
    @Override
    public void computeEnhancements(ContentItem ci) throws EngineException {
        try {
            MGraph g = ci.getMetadata();
            LiteralFactory literalFactory = LiteralFactory.getInstance();

            CategoryList categories = apiProvider.categories(ci.getStream(),
                    ci.getMimeType());
            for (Category category : categories.getCategories()) {
                UriRef keywordEnhancement = EnhancementEngineHelper.createEntityEnhancement(
                        ci, this);
                g.add(new TripleImpl(keywordEnhancement,
                        Properties.FISE_ENTITY_TYPE, new UriRef(
                                SalsadevConstants.SALSADEV_NS_CATEGORY)));
                g.add(new TripleImpl(keywordEnhancement, new UriRef(
                        SalsadevConstants.SALSADEV_NS_NAME),
                        literalFactory.createTypedLiteral(category.getName())));
                g.add(new TripleImpl(keywordEnhancement, new UriRef(
                        SalsadevConstants.SALSADEV_NS_SCORE),
                        literalFactory.createTypedLiteral(category.getScore())));
            }
        } catch (SalsadevApiException e) {
            throw new EngineException(e.getLocalizedMessage(), e);
        }
    }

}
