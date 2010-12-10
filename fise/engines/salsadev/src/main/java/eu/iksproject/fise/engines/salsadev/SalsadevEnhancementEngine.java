package eu.iksproject.fise.engines.salsadev;

import eu.iksproject.fise.engines.salsadev.core.SalsadevApiException;
import eu.iksproject.fise.engines.salsadev.core.SalsadevApiProvider;
import eu.iksproject.fise.engines.salsadev.core.SalsadevConstants;
import eu.iksproject.fise.engines.salsadev.core.xml.pojo.Category;
import eu.iksproject.fise.engines.salsadev.core.xml.pojo.CategoryList;
import eu.iksproject.fise.engines.salsadev.core.xml.pojo.Keyword;
import eu.iksproject.fise.engines.salsadev.core.xml.pojo.KeywordList;

import eu.iksproject.fise.servicesapi.ContentItem;
import eu.iksproject.fise.servicesapi.EngineException;
import eu.iksproject.fise.servicesapi.EnhancementEngine;
import eu.iksproject.fise.servicesapi.ServiceProperties;
import eu.iksproject.fise.servicesapi.helper.EnhancementEngineHelper;
import eu.iksproject.fise.servicesapi.rdf.Properties;

import org.apache.clerezza.rdf.core.LiteralFactory;
import org.apache.clerezza.rdf.core.MGraph;
import org.apache.clerezza.rdf.core.UriRef;
import org.apache.clerezza.rdf.core.impl.TripleImpl;

import java.io.IOException;

import java.util.Collections;
import java.util.Dictionary;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

import org.osgi.service.component.ComponentContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link SalsadevEnhancementEngine} uses SalsaDev Engine to provide keywords, categories and some other enhancements
 * for content.
 *
 * @author <a href="mailto:aleksey.oborin@salsadev.com">Aleksey Oborin</a>
 * @version %I%, %G%
 */
@Component(immediate = true, metatype = true)
@Service
public class SalsadevEnhancementEngine implements EnhancementEngine, ServiceProperties {
    /**
     * SalsadevApiProvider, provides methods to work with SalsaDev API.
     */
    private SalsadevApiProvider apiProvider;
    /**
     * This contains the logger.
     */
    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(SalsadevEnhancementEngine.class);
    /**
     * Holds the property name of the property to configuration file path name.
     */
    @Property
    private static final String PROPERTIES_FILE_PATH = "eu.iksproject.fise.engines.salsadev.properties.file.path";
    /**
     * The default value for the Execution of this Engine. Currently set to
     * {@link ServiceProperties#ORDERING_PRE_PROCESSING}
     */
    public static final Integer DEFAULT_ORDER = ServiceProperties.ORDERING_PRE_PROCESSING - 17;

    /**
     * The activate method.
     *
     * @param ce the {@link org.osgi.service.component.ComponentContext}
     * @throws java.io.IOException if initializing fails
     */
    @SuppressWarnings("unused, unchecked")
    protected void activate(@SuppressWarnings("unused") ComponentContext ce) throws IOException {
        Dictionary<String, String> properties = ce.getProperties();
        String propertiesFilePath = properties.get(PROPERTIES_FILE_PATH);
        if (StringUtils.isEmpty(propertiesFilePath)) {
            this.apiProvider = new SalsadevApiProvider();
        } else {
            this.apiProvider = new SalsadevApiProvider(propertiesFilePath);
        }
    }

    /**
     * The deactivate method.
     *
     * @param ce the {@link ComponentContext}
     */
    @SuppressWarnings("unused")
    protected void deactivate(@SuppressWarnings("unused") ComponentContext ce) {
        this.apiProvider = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int canEnhance(ContentItem ci) throws EngineException {
        if (this.apiProvider.isSupported(ci.getMimeType())) {
            return ENHANCE_SYNCHRONOUS;
        }
        return CANNOT_ENHANCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void computeEnhancements(ContentItem ci) throws EngineException {
        try {
            apiProvider.index(ci.getStream(), ci.getMimeType(), ci.getId());

            MGraph g = ci.getMetadata();
            LiteralFactory literalFactory = LiteralFactory.getInstance();

            KeywordList keywords = apiProvider.keywords(ci.getStream(), ci.getMimeType());
            for (Keyword keyword : keywords.getKeywords()) {
                UriRef keywordEnhancement = EnhancementEngineHelper.createEntityEnhancement(ci, this);
                g.add(new TripleImpl(keywordEnhancement, Properties.FISE_ENTITY_TYPE,
                        new UriRef(SalsadevConstants.SALSADEV_NS_KEYWORD)));
                g.add(new TripleImpl(keywordEnhancement, new UriRef(SalsadevConstants.SALSADEV_NS_TERM),
                        literalFactory.createTypedLiteral(keyword.getTerm())));
                g.add(new TripleImpl(keywordEnhancement, new UriRef(SalsadevConstants.SALSADEV_NS_SCORE),
                        literalFactory.createTypedLiteral(keyword.getScore())));
            }

            CategoryList categories = apiProvider.categories(ci.getStream(), ci.getMimeType());
            for (Category category : categories.getCategories()) {
                UriRef keywordEnhancement = EnhancementEngineHelper.createEntityEnhancement(ci, this);
                g.add(new TripleImpl(keywordEnhancement, Properties.FISE_ENTITY_TYPE,
                        new UriRef(SalsadevConstants.SALSADEV_NS_CATEGORY)));
                g.add(new TripleImpl(keywordEnhancement, new UriRef(SalsadevConstants.SALSADEV_NS_NAME),
                        literalFactory.createTypedLiteral(category.getName())));
                g.add(new TripleImpl(keywordEnhancement, new UriRef(SalsadevConstants.SALSADEV_NS_SCORE),
                        literalFactory.createTypedLiteral(category.getScore())));
            }
        } catch (SalsadevApiException e) {
            throw new EngineException(e.getLocalizedMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> getServiceProperties() {
        return Collections.unmodifiableMap(Collections.singletonMap(ServiceProperties.ENHANCEMENT_ENGINE_ORDERING,
                (Object) DEFAULT_ORDER));
    }
}
