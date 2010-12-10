package eu.iksproject.fise.engines.salsadev.core;

/**
 * {@link SalsadevConstants} Interface represents SalsaDev constants.
 *
 * @author <a href="mailto:aleksey.oborin@salsadev.com">Aleksey Oborin</a>
 * @version %I%, %G%
 */
public interface SalsadevConstants {
    String DEFAULT_PROPERTIES_FILE = "config.properties";

    String MIME_TYPES_PROPERTY_NAME = "mime_types.supported";

    String HOST_PROPERTY_NAME = "host";

    String PROTOCOL_PROPERTY_NAME = "protocol";

    String LOGIN_PROPERTY_NAME = "login";

    String PASSWORD_PROPERTY_NAME = "password";

    String PORT_PROPERTY_NAME = "port";

    String CONTEXT_PROPERTY_NAME = "context";

    String KEYWORDS_NUM_PROPERTY_NAME = "keywords.number";

    String CATEGORIES_NUM_PROPERTY_NAME = "categories.number";

    String CATEGORIES_THRESHOLD_PROPERTY_NAME = "categories.threshold";

    String SALSADEV_NS_URI = "http://salsadev.com/ns#";

    String SALSADEV_NS_KEYWORD = "http://salsadev.com/ns#Keyword";

    String SALSADEV_NS_CATEGORY = "http://salsadev.com/ns#Category";

    String SALSADEV_NS_TERM = "http://salsadev.com/ns#term";

    String SALSADEV_NS_NAME = "http://salsadev.com/ns#name";

    String SALSADEV_NS_SCORE = "http://salsadev.com/ns#score";
}
