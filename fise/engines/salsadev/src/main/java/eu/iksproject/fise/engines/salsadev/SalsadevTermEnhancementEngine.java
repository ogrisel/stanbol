package eu.iksproject.fise.engines.salsadev;

import org.apache.clerezza.rdf.core.LiteralFactory;
import org.apache.clerezza.rdf.core.MGraph;
import org.apache.clerezza.rdf.core.UriRef;
import org.apache.clerezza.rdf.core.impl.TripleImpl;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;

import eu.iksproject.fise.engines.salsadev.core.SalsadevApiException;
import eu.iksproject.fise.engines.salsadev.core.SalsadevConstants;
import eu.iksproject.fise.engines.salsadev.core.xml.pojo.Keyword;
import eu.iksproject.fise.engines.salsadev.core.xml.pojo.KeywordList;
import eu.iksproject.fise.servicesapi.ContentItem;
import eu.iksproject.fise.servicesapi.EngineException;
import eu.iksproject.fise.servicesapi.EnhancementEngine;
import eu.iksproject.fise.servicesapi.helper.EnhancementEngineHelper;
import eu.iksproject.fise.servicesapi.rdf.Properties;

/**
 * Extract the main terms / tags of a text document using the Salsadev public
 * API.
 */
@Component(immediate = true, metatype = true)
@Service
public class SalsadevTermEnhancementEngine extends
        AbstractSalsadevEnhancementEngine implements EnhancementEngine {

    /**
     * {@inheritDoc}
     */
    @Override
    public void computeEnhancements(ContentItem ci) throws EngineException {
        try {
            MGraph g = ci.getMetadata();
            LiteralFactory literalFactory = LiteralFactory.getInstance();

            KeywordList keywords = apiProvider.keywords(ci.getStream(),
                    ci.getMimeType());
            for (Keyword keyword : keywords.getKeywords()) {
                UriRef keywordEnhancement = EnhancementEngineHelper.createEntityEnhancement(
                        ci, this);
                g.add(new TripleImpl(keywordEnhancement,
                        Properties.FISE_ENTITY_TYPE, new UriRef(
                                SalsadevConstants.SALSADEV_NS_KEYWORD)));
                g.add(new TripleImpl(keywordEnhancement, new UriRef(
                        SalsadevConstants.SALSADEV_NS_TERM),
                        literalFactory.createTypedLiteral(keyword.getTerm())));
                g.add(new TripleImpl(keywordEnhancement, new UriRef(
                        SalsadevConstants.SALSADEV_NS_SCORE),
                        literalFactory.createTypedLiteral(keyword.getScore())));
            }
        } catch (SalsadevApiException e) {
            throw new EngineException(e.getLocalizedMessage(), e);
        }
    }
}
