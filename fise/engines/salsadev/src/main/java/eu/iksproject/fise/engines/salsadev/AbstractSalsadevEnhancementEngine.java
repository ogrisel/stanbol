package eu.iksproject.fise.engines.salsadev;

import java.io.IOException;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Property;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.iksproject.fise.engines.salsadev.core.SalsadevApiProvider;
import eu.iksproject.fise.servicesapi.ContentItem;
import eu.iksproject.fise.servicesapi.EngineException;
import eu.iksproject.fise.servicesapi.EnhancementEngine;
import eu.iksproject.fise.servicesapi.ServiceProperties;

/**
 * Abstract base class for the Salsadev enhancement engines
 */
public abstract class AbstractSalsadevEnhancementEngine implements
        EnhancementEngine, ServiceProperties {

    /**
     * SalsadevApiProvider, provides methods to work with SalsaDev API.
     */
    protected SalsadevApiProvider apiProvider;

    /**
     * This contains the logger.
     */
    @SuppressWarnings("unused")
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSalsadevEnhancementEngine.class);

    /**
     * Holds the property name of the property to configuration file path name.
     */
    @Property
    protected static final String PROPERTIES_FILE_PATH = "eu.iksproject.fise.engines.salsadev.properties.file.path";

    /**
     * The default value for the Execution of this Engine. Currently set to
     * {@link ServiceProperties#ORDERING_EXTRACTION_ENHANCEMENT}
     */
    public static final Integer DEFAULT_ORDER = ServiceProperties.ORDERING_EXTRACTION_ENHANCEMENT;

    /**
     * The activate method.
     *
     * @param ce the {@link org.osgi.service.component.ComponentContext}
     * @throws java.io.IOException if initializing fails
     */
    @SuppressWarnings("unchecked")
    protected void activate(ComponentContext ce) throws IOException {
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
    protected void deactivate(ComponentContext ce) {
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
    public Map<String, Object> getServiceProperties() {
        return Collections.unmodifiableMap(Collections.singletonMap(
                ServiceProperties.ENHANCEMENT_ENGINE_ORDERING,
                (Object) DEFAULT_ORDER));
    }
}
