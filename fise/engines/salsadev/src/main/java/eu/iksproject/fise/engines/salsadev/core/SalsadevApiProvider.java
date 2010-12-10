package eu.iksproject.fise.engines.salsadev.core;

import com.thoughtworks.xstream.XStream;

import eu.iksproject.fise.engines.salsadev.core.xml.converter.CategoryConverter;
import eu.iksproject.fise.engines.salsadev.core.xml.converter.DocumentConverter;
import eu.iksproject.fise.engines.salsadev.core.xml.converter.KeywordConverter;
import eu.iksproject.fise.engines.salsadev.core.xml.converter.SearchDescriptorConverter;
import eu.iksproject.fise.engines.salsadev.core.xml.pojo.*;
import eu.iksproject.fise.engines.salsadev.core.xml.pojo.Category;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link SalsadevApiProvider} Provides methods to do SalsaDev API requests.
 *
 * @author <a href="mailto:aleksey.oborin@salsadev.com">Aleksey Oborin</a>
 * @version %I%, %G%
 */
@SuppressWarnings("unused")
public class SalsadevApiProvider {
    /**
     * This contains the logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SalsadevApiProvider.class);

    /**
     * Set of supportable content mime types.
     */
    private Set<String> mimeTypes = new HashSet<String>();
    /**
     * Host of SalsaDev API account.
     */
    private String host;
    /**
     * Port of SalsaDev API account.
     */
    private int port;
    /**
     * Protocol of SalsaDev API account.
     */
    private String protocol;
    /**
     * Login of SalsaDev API account.
     */
    private String login;
    /**
     * Password of SalsaDev API account.
     */
    private String password;
    /**
     * Context of SalsaDev API account.
     */
    private String context;
    /**
     * Number of keywords that should be return.
     */
    private int keywordsNumber = 10; //default value
    /**
     * Number of categories that should be return.
     */
    private int categoriesNumber = 5; //default value
    /**
     * Threshold value that should be used to get categories. 
     */
    private double categoriesThreshold = 0.2d; //default value

    /**
     * Initializes ApiProvider with configuration properties from the specified properties file.
     *
     * @param propertiesFilePath path to the properties file.
     * @throws IOException in case of any IO errors.
     */
    public SalsadevApiProvider(String propertiesFilePath) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileReader(propertiesFilePath));
        loadProperties(properties);
    }

    /**
     * Initializes ApiProvider with default configuration properties.
     *
     * @throws IOException in case of any IO errors.
     */
    public SalsadevApiProvider() throws IOException {
        InputStream in =
                getClass().getClassLoader().getResourceAsStream(SalsadevConstants.DEFAULT_PROPERTIES_FILE);
        Properties properties = new Properties();
        properties.load(in);
        loadProperties(properties);
    }

    /**
     * Loads properties values.
     *
     * @param properties properties to be loaded.
     * @throws java.io.IOException in case of there is no some property defined.
     */
    private void loadProperties(Properties properties) throws IOException {
        if (properties.getProperty(SalsadevConstants.MIME_TYPES_PROPERTY_NAME) != null) {
            String[] mimeTypesValues = properties.getProperty(SalsadevConstants.MIME_TYPES_PROPERTY_NAME).split(",");
            mimeTypes.addAll(Arrays.asList(mimeTypesValues));
        } else {
            throw new IOException("There is no " + SalsadevConstants.MIME_TYPES_PROPERTY_NAME + " property specified.");
        }

        if (properties.getProperty(SalsadevConstants.HOST_PROPERTY_NAME) != null) {
            this.host = properties.getProperty(SalsadevConstants.HOST_PROPERTY_NAME);
        } else {
            throw new IOException("There is no " + SalsadevConstants.HOST_PROPERTY_NAME + " property specified.");
        }

        if (properties.getProperty(SalsadevConstants.PORT_PROPERTY_NAME) != null) {
            this.port = Integer.valueOf(properties.getProperty(SalsadevConstants.PORT_PROPERTY_NAME));
        } else {
            throw new IOException("There is no " + SalsadevConstants.PORT_PROPERTY_NAME + " property specified.");
        }

        if (properties.getProperty(SalsadevConstants.PROTOCOL_PROPERTY_NAME) != null) {
            this.protocol = properties.getProperty(SalsadevConstants.PROTOCOL_PROPERTY_NAME);
        } else {
            throw new IOException("There is no " + SalsadevConstants.PROTOCOL_PROPERTY_NAME + " property specified.");
        }

        if (properties.getProperty(SalsadevConstants.LOGIN_PROPERTY_NAME) != null) {
            this.login = properties.getProperty(SalsadevConstants.LOGIN_PROPERTY_NAME);
        } else {
            throw new IOException("There is no " + SalsadevConstants.LOGIN_PROPERTY_NAME + " property specified.");
        }

        if (properties.getProperty(SalsadevConstants.PASSWORD_PROPERTY_NAME) != null) {
            this.password = properties.getProperty(SalsadevConstants.PASSWORD_PROPERTY_NAME);
        } else {
            throw new IOException("There is no " + SalsadevConstants.PASSWORD_PROPERTY_NAME + " property specified.");
        }

        if (properties.getProperty(SalsadevConstants.CONTEXT_PROPERTY_NAME) != null) {
            this.context = properties.getProperty(SalsadevConstants.CONTEXT_PROPERTY_NAME);
        }

        if (properties.getProperty(SalsadevConstants.KEYWORDS_NUM_PROPERTY_NAME) != null) {
            this.keywordsNumber = Integer.valueOf(properties.getProperty(
                    SalsadevConstants.KEYWORDS_NUM_PROPERTY_NAME));
        }

        if (properties.getProperty(SalsadevConstants.CATEGORIES_NUM_PROPERTY_NAME) != null) {
            this.categoriesNumber = Integer.valueOf(properties.getProperty(
                    SalsadevConstants.CATEGORIES_NUM_PROPERTY_NAME));
        }

        if (properties.getProperty(SalsadevConstants.CATEGORIES_THRESHOLD_PROPERTY_NAME) != null) {
            this.categoriesThreshold = Double.valueOf(properties.getProperty(
                    SalsadevConstants.CATEGORIES_THRESHOLD_PROPERTY_NAME));
        }
    }

    /**
     * Checks if the specified mime type is supported by the SalsaDev engine.
     *
     * @param mimeType mime type.
     * @return a boolean.
     */
    public boolean isSupported(String mimeType) {
        return mimeTypes.contains(mimeType);
    }

    /**
     * Indexes the specified content with SalsaDev search engine.
     *
     * @param contentStream stream with content.
     * @param contentType   content type.
     * @param uid           unique id of content.
     * @throws SalsadevApiException in case of any errors during api call.
     */
    public void index(InputStream contentStream, String contentType, String uid) throws SalsadevApiException {
        PostMethod postMethod = null;
        try {
            postMethod = new PostMethod(new StringBuilder().append(buildApiUrl()).append("/index?uid=").
                    append(URLEncoder.encode(uid, "utf8")).toString());
            postMethod.setDoAuthentication(true);
            postMethod.setRequestHeader("Content-Type", contentType);
            postMethod.setRequestBody(contentStream);

            prepareHttpClient().executeMethod(postMethod);

        } catch (Exception e) {
            LOGGER.warn(e.getLocalizedMessage(), e);
            throw new SalsadevApiException(e.getLocalizedMessage(), e);
        } finally {
            if (postMethod != null) {
                postMethod.releaseConnection();
            }
        }
    }

    /**
     * Finds suitable keywords for the given content.
     *
     * @param contentStream stream with content.
     * @param contentType   content type.
     * @return KeywordListDto object set with results.
     * @throws SalsadevApiException in case of any errors during api call.
     */
    public KeywordList keywords(InputStream contentStream, String contentType) throws SalsadevApiException {
        PostMethod postMethod = null;
        try {
            postMethod = new PostMethod(new StringBuilder().append(buildApiUrl()).append("/lift/keywords?num=").
                    append(keywordsNumber).toString());
            postMethod.setDoAuthentication(true);
            postMethod.setRequestHeader("Content-Type", contentType);
            postMethod.setRequestBody(contentStream);

            prepareHttpClient().executeMethod(postMethod);

            LOGGER.info(postMethod.getResponseBodyAsString());
            InputStream responseStream = postMethod.getResponseBodyAsStream();

            XStream xstream = new XStream();
            xstream.alias("keywords", KeywordList.class);
            xstream.addImplicitCollection(KeywordList.class, "keywords");
            xstream.registerConverter(new KeywordConverter());
            xstream.alias("keyword", Keyword.class);
            KeywordList keywordList = (KeywordList) xstream.fromXML(responseStream);

            if (keywordList == null || keywordList.getKeywords() == null) {
                keywordList = new KeywordList();
                keywordList.setKeywords(new HashSet<Keyword>());
            }
            return keywordList;
        } catch (Exception e) {
            LOGGER.warn(e.getLocalizedMessage(), e);
            throw new SalsadevApiException(e.getLocalizedMessage(), e);
        } finally {
            if (postMethod != null) {
                postMethod.releaseConnection();
            }
        }
    }

    /**
     * Categorizes given content.
     *
     * @param contentStream stream with content.
     * @param contentType   content type.
     * @return CategoryHitListDto set with the results.
     * @throws SalsadevApiException in case of any errors during api call.
     */
    public CategoryList categories(InputStream contentStream, String contentType) throws SalsadevApiException {

        PostMethod postMethod = null;
        try {
            postMethod = new PostMethod(new StringBuilder().append(buildApiUrl()).append("/categorize?num=").
                    append(categoriesNumber).append("&rt=").append(categoriesThreshold).toString());
            postMethod.setDoAuthentication(true);
            postMethod.setRequestHeader("Content-Type", contentType);
            postMethod.setRequestBody(contentStream);

            prepareHttpClient().executeMethod(postMethod);

            LOGGER.info(postMethod.getResponseBodyAsString());
            InputStream responseStream = postMethod.getResponseBodyAsStream();

            XStream xStream = new XStream();
            xStream.alias("categoryHits", CategoryList.class);
            xStream.addImplicitCollection(CategoryList.class, "categories");
            xStream.registerConverter(new CategoryConverter());
            xStream.alias("categoryHit", Category.class);
            CategoryList categoryListDto = (CategoryList) xStream.fromXML(responseStream);

            if (categoryListDto == null || categoryListDto.getCategories() == null) {
                categoryListDto = new CategoryList();
                categoryListDto.setCategories(new ArrayList<Category>());
            }
            return categoryListDto;
        } catch (Exception e) {
            LOGGER.warn(e.getLocalizedMessage(), e);
            throw new SalsadevApiException(e.getLocalizedMessage(), e);
        } finally {
            if (postMethod != null) {
                postMethod.releaseConnection();
            }
        }
    }

    /**
     * Searches the specified query with SalsaDev search engine.
     *
     * @param query  query to be searched.
     * @param limit  number of results.
     * @param offset offset of the results.
     * @return DocumentHitListDto object set with search results.
     * @throws SalsadevApiException in case of any errors during api call.
     */
    public DocumentList search(String query, int limit, int offset) throws SalsadevApiException {
        GetMethod getMethod = null;
        try {
            String resultSize = "&num=" + Integer.MAX_VALUE;
            String pageNumber = "";
            if (limit > 0) {
                resultSize = "&num=" + limit;
            }
            if (offset > 0) {
                pageNumber = "&page=" + offset;
            }

            getMethod = new GetMethod(new StringBuilder().append(buildApiUrl()).append("/search?query=").
                    append(URLEncoder.encode(query, "utf8")).
                    append(resultSize).append(pageNumber).toString());
            getMethod.setDoAuthentication(true);
            prepareHttpClient().executeMethod(getMethod);

            InputStream responseStream = getMethod.getResponseBodyAsStream();

            XStream xStream = new XStream();
            xStream.alias("documentHits", DocumentList.class);
            xStream.addImplicitCollection(DocumentList.class, "documents");
            xStream.registerConverter(new DocumentConverter());
            xStream.alias("documentHit", Document.class);

            DocumentList documentList = (DocumentList) xStream.fromXML(responseStream);

            if (documentList == null || documentList.getDocuments() == null) {
                documentList = new DocumentList();
                documentList.setDocuments(new ArrayList<Document>());
            }
            return documentList;
        } catch (Exception e) {
            LOGGER.error("Error while trying to perform a search.", e);
            LOGGER.warn(e.getLocalizedMessage(), e);
            throw new SalsadevApiException(e.getLocalizedMessage(), e);
        } finally {
            if (getMethod != null) {
                getMethod.releaseConnection();
            }
        }
    }

    /**
     * Searches for content using specified search descriptor.
     *
     * @param searchDescriptor search descriptor filled with constraints.
     * @return DocumentHitListDto object set with search results.
     * @throws SalsadevApiException in case of any errors during api call.
     */
    public DocumentList constrainSearch(SearchDescriptor searchDescriptor) throws SalsadevApiException {
        PostMethod postMethod = null;
        try {
            postMethod = new PostMethod("/search");
            postMethod.setDoAuthentication(true);
            postMethod.setRequestHeader("Content-Type", "application/xml");

            XStream xStream = new XStream();
            xStream.alias("documentHits", DocumentList.class);
            xStream.addImplicitCollection(DocumentList.class, "documents");
            xStream.registerConverter(new DocumentConverter());
            xStream.alias("documentHit", Document.class);
            xStream.registerConverter(new SearchDescriptorConverter());

            postMethod.setRequestBody(xStream.toXML(searchDescriptor));

            prepareHttpClient().executeMethod(postMethod);

            LOGGER.info(postMethod.getResponseBodyAsString());
            InputStream responseStream = postMethod.getResponseBodyAsStream();

            DocumentList documentList = (DocumentList) xStream.fromXML(responseStream);

            if (documentList == null || documentList.getDocuments() == null) {
                documentList = new DocumentList();
                documentList.setDocuments(new ArrayList<Document>());
            }
            return documentList;

        } catch (Exception e) {
            LOGGER.error("Error while trying to perform a constrain search.", e);
            LOGGER.warn(e.getLocalizedMessage(), e);
            throw new SalsadevApiException(e.getLocalizedMessage(), e);
        } finally {
            if (postMethod != null) {
                postMethod.releaseConnection();
            }
        }
    }

                                                      
    /**
     * Builds url string to SalsaDev api.
     *
     * @return api url string.
     */
    private String buildApiUrl() {
        return new StringBuilder().append(protocol).append("://").append(host).append(":").append(port).
                append(StringUtils.isEmpty(context) ? "" : "/" + context).toString();
    }

    /**
     * Prepares HttpClient with basic auth params.
     *
     * @return HttpClient.
     */
    public HttpClient prepareHttpClient() {
        HttpClient client = new HttpClient();
        client.getParams().setAuthenticationPreemptive(true);
        Credentials credentials = new UsernamePasswordCredentials(login, password);
        client.getState().setCredentials(new AuthScope(host, port, AuthScope.ANY_REALM), credentials);
        return client;
    }
}
