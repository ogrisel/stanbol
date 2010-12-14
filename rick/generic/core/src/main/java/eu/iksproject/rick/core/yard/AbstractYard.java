package eu.iksproject.rick.core.yard;

import java.util.Dictionary;
import java.util.Hashtable;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.osgi.service.cm.ConfigurationException;

import eu.iksproject.rick.core.query.DefaultQueryFactory;
import eu.iksproject.rick.core.utils.ModelUtils;
import eu.iksproject.rick.servicesapi.model.Representation;
import eu.iksproject.rick.servicesapi.model.ValueFactory;
import eu.iksproject.rick.servicesapi.query.FieldQueryFactory;
import eu.iksproject.rick.servicesapi.yard.Yard;
@Component(componentAbstract=true)
@Properties(value={
        @Property(name=Yard.ID,value="rickYard"),
        @Property(name=Yard.NAME,value="Rick Yard"),
        @Property(name=Yard.DESCRIPTION,value="Default values for configuring the RickYard without editing"),
        @Property(name=AbstractYard.DEFAULT_QUERY_RESULT_NUMBER,intValue=-1),
        @Property(name=AbstractYard.MAX_QUERY_RESULT_NUMBER,intValue=-1)
})
public abstract class AbstractYard implements Yard {

    /**
     * Key used to configure maximum number of query results supported by
     * this yard.<br>
     * The default (if not set) is set to {@link #SolrQueryFactoy#MAX_QUERY_RESULT_NUMBER}
     * ({@value #SolrQueryFactoy#MAX_QUERY_RESULT_NUMBER})
     */
    public static final String MAX_QUERY_RESULT_NUMBER = "eu.iksproject.rick.yard.maxQueryResultNumber";
    /**
     * Key used to configure the default number of query results supported by
     * this yard. <br>
     * The default (if not set) is set to {@link #SolrQueryFactoy#DEFAULT_QUERY_RESULT_NUMBER}
     * ({@value #SolrQueryFactoy#DEFAULT_QUERY_RESULT_NUMBER})
     */
    public static final String DEFAULT_QUERY_RESULT_NUMBER = "eu.iksproject.rick.yard.defaultQueryResultNumber";
    /**
     * This Yard uses the default in-memory implementation of the RICK model.
     */
    protected ValueFactory valueFactory;
    /**
     * The QueryFactory as required by {@link Yard#getQueryFactory()}. This
     * Yard uses the default implementation as provided by the
     * {@link DefaultQueryFactory}.
     */
    protected FieldQueryFactory queryFactory;
    /**
     * Holds the configuration of the Yard.
     */
    protected YardConfig config;

    /**
     * Default constructor to create an uninitialised Yard. Typically used
     * within an OSGI environment
     */
    protected AbstractYard(){}

//    /**
//     * Constructor to create an initialised Yard.
//     * @param valueFactory The value factory for the yard
//     * @param queryFactory The query factory for the yard
//     * @param config The configuration of the yard
//     * @throws IllegalArgumentException if any of the three parameter is <code>null</code>
//     */
//    protected AbstractYard(ValueFactory valueFactory,FieldQueryFactory queryFactory, YardConfig config) throws IllegalArgumentException{
//        activate(valueFactory, queryFactory, config);
//    }

    /**
     * Activates the Yard based on the parsed parameter. Typically called within
     * an OSGI environment by the activate method. Internally called by the
     * {@link #AbstractYard(ValueFactory, FieldQueryFactory, YardConfig)}
     * constructor.
     * @param valueFactory The value factory for the yard
     * @param queryFactory The query factory for the yard
     * @param config The configuration of the yard
     */
    protected final void activate(ValueFactory valueFactory,FieldQueryFactory queryFactory, YardConfig config) {
        if(valueFactory == null){
            throw new IllegalArgumentException("Unable to activate: The ValueFactory MUST NOT be NULL!");
        }
        if(queryFactory == null){
            throw new IllegalArgumentException("Unable to activate: The QueryFactory MUST NOT be NULL!");
        }
        if(config == null){
            throw new IllegalArgumentException("Unable to activate: The YardConfig MUST NOT be NULL!");
        }
        this.queryFactory = queryFactory;
        this.valueFactory = valueFactory;
        this.config = config;
    }
    /**
     * Deactivates this yard instance. Typically called within an OSGI environment
     * by the deacivate method.
     *
     */
    protected final void deactivate(){
        this.queryFactory = null;
        this.valueFactory = null;
        this.config = null;
    }

    /**
     * Creates a new representation with a random uuid by using the pattern:
     * <code><pre>
     *   urn:eu.iksproject:rick.yard.&lt;getClass().getSimpleName()&gt;:&lt;getId()&gt;.&lt;uuid&gt;
     * </pre></code>
     * @see Yard#create()
     */
    @Override
    public final Representation create() {
        return create(null);
    }
    /**
     * Creates a representation with the parsed ID. If <code>null</code> is
     * parsed a random UUID is generated as describe in {@link #create()}.
     * @param id The id or <code>null</code> to create a random uuid
     * @see Yard#create(String)
     */
    @Override
    public final Representation create(String id) throws IllegalArgumentException {
        if(config == null){
            throw new IllegalStateException("This Yard is not activated");
        }
        if(id == null){
            id = String.format("urn:eu.iksproject:rick.yard.%s:%s.%s",
                    getClass().getSimpleName(),
                    config.getId(),
                    ModelUtils.randomUUID().toString());
        }
        return valueFactory.createRepresentation(id);
    }


    @Override
    public final String getDescription() {
        if(config == null){
            throw new IllegalStateException("This Yard is not activated");
        }
        return config.getDescription();
    }

    @Override
    public final String getId() {
        if(config == null){
            throw new IllegalStateException("This Yard is not activated");
        }
        return config.getId();
    }

    @Override
    public final String getName() {
        if(config == null){
            throw new IllegalStateException("This Yard is not activated");
        }
        return config.getName();
    }

    @Override
    public final FieldQueryFactory getQueryFactory() {
        if(queryFactory == null){
            throw new IllegalStateException("This Yard is not activated");
        }
        return queryFactory;
    }
    @Override
    public final ValueFactory getValueFactory() {
        if(valueFactory == null){
            throw new IllegalStateException("This Yard is not activated");
        }
        return valueFactory;
    }

    /** ------------------------------------------------------------------------
     *    Methods that need to be implemented by Sub-Classes
     *  ------------------------------------------------------------------------
     */
//    @Override
//    public abstract QueryResultList<Representation> find(FieldQuery query);
//    @Override
//    public abstract QueryResultList<String> findReferences(FieldQuery query);
//    @Override
//    public abstract QueryResultList<Representation> findRepresentation(FieldQuery query);
//    @Override
//    public abstract Representation getRepresentation(String id);
//    @Override
//    public abstract boolean isRepresentation(String id);
//    @Override
//    public abstract void remove(String id) throws IllegalArgumentException;
//    @Override
//    public abstract void store(Representation representation) throws IllegalArgumentException;
//    @Override
//    public abstract void update(Representation represnetation) throws IllegalArgumentException;


    public static abstract class YardConfig {

        protected final Dictionary<String, Object> config;

        /**
         * Creates a new config with the minimal set of required properties
         * @param id the ID of the Yard
         * @throws IllegalArgumentException if the parsed valued do not fulfil the
         * requirements.
         */
        protected YardConfig(String id) throws IllegalArgumentException{
            this.config = new Hashtable<String, Object>();
            setId(id);
        }
        /**
         * Initialise the Yard configuration based on a parsed configuration. Usually
         * used on the context of an OSGI environment in the activate method.
         * @param config the configuration usually parsed within an OSGI activate
         * method
         * @throws ConfigurationException if the configuration is incomplete of
         * some values are not valid
         * @throws IllegalArgumentException if <code>null</code> is parsed as
         * configuration
         */
        protected YardConfig(Dictionary<String, Object> config) throws ConfigurationException,IllegalArgumentException {
            if(config == null){
                throw new IllegalArgumentException("The parsed configuration MUST NOT be NULL");
            }
            this.config = config;
            isValid();
        }
        /**
         * Setter for the ID of the yard. The id is usually a sort name such as
         * "dbPedia", "freebase", "geonames.org", "my.projects" ...<p>
         * If {@link #isMultiYardIndexLayout()} than this ID is used to identify
         * Representations of this Yard within the SolrIndex.
         * @param the id of the yard. Required, not null, not empty!
         */
        public void setId(String id) {
            if(id != null){
                config.put(Yard.ID, id);
            } else {
                config.remove(Yard.ID);
            }
        }

        /**
         * Getter for the ID of the yard
         * @return the id of the yard
         */
        public String getId() {
            Object value = config.get(Yard.ID);
            return value==null?null:value.toString();
        }

        /**
         * Setter for the name of this yard. If not set the {@link #getId(String)}
         * is used as default
         * @param name The name or <code>null</code> to use {@link #getId()}.
         */
        public void setName(String name) {
            if(name != null){
                config.put(Yard.NAME, name);
            } else {
                config.remove(Yard.NAME);
            }
        }

        /**
         * Getter for the human readable name of the Yard
         * @return the name
         */
        public String getName() {
            Object value = config.get(Yard.NAME);
            return value==null?getId():value.toString();
        }

        /**
         * Setter for the description of this Yard
         * @param description the description. Optional parameter
         */
        public void setDescription(String description) {
            if(description != null){
                config.put(Yard.DESCRIPTION, description);
            } else {
                config.remove(Yard.DESCRIPTION);
            }
        }

        /**
         * Getter for the description
         * @return description The description or <code>null</code> if not defined
         */
        public String getDescription() {
            Object value = config.get(Yard.DESCRIPTION);
            return value==null?null:value.toString();
        }

        /**
         * Setter for the default number of query results. This is used if parsed
         * queries do not define a limit for the maximum number of results.
         * @param defaultQueryResults the default number of query results.
         * <code>null</code> or a negative number to use the default value defined
         * by the Yard.
         */
        public void setDefaultQueryResultNumber(Integer defaultQueryResults) {
            if(defaultQueryResults != null){
                config.put(DEFAULT_QUERY_RESULT_NUMBER, defaultQueryResults);
            } else {
                config.remove(DEFAULT_QUERY_RESULT_NUMBER);
            }
        }

        /**
         * Getter for the default number of query results. This is used if parsed
         * queries do not define a limit for the maximum number of results.<p>
         * If {@link #getMaxQueryResultNumber()} is defines (>0), than this
         * method returns the minimum of the two configured values.
         * @return the default number used as the maximum number of results per
         * query if not otherwise set by the parsed query. Returns <code>0</code>
         * if the value was set to a number lower or equals 0 and -1 if the
         * value is not configured at all.
         */
        public int getDefaultQueryResultNumber() throws NumberFormatException {
            Object value = config.get(DEFAULT_QUERY_RESULT_NUMBER);
            Integer number;
            if(value != null){
                if(value instanceof Integer){
                    number = (Integer) value;
                } else {
                    try {
                        number = new Integer(value.toString());
                    } catch (NumberFormatException e){
                        return -1;
                    }
                }
            } else {
                return -1;
            }
            if(number.intValue() <= 0){
                return 0;
            } else {
                if(getMaxQueryResultNumber() > 0){
                    return Math.min(getMaxQueryResultNumber(), number);
                } else {
                    return number;
                }
            }
        }

        /**
         * Setter for the maximum number of query results. This is used to limit the
         * maximum number of results when parsed queries define limits that are
         * greater this value.
         * @param maxQueryResults The maximum number of results for queries.
         * <code>null</code> or a negative number to use the default as defined by
         * the Yard implementation.
         */
        public void setMaxQueryResultNumber(Integer maxQueryResults) {
            if(maxQueryResults != null){
                config.put(MAX_QUERY_RESULT_NUMBER, maxQueryResults);
            } else {
                config.remove(MAX_QUERY_RESULT_NUMBER);
            }
        }

        /**
         * Getter for the maximum number of query results.This is used to limit the
         * maximum number of results when parsed queries define limits that are
         * greater this value.
         * @return the maximum number of query results. Returns <code>0</code>
         * if the value was set to a number lower or equals 0 and -1 if the
         * value is not configured at all.
         */
        public int getMaxQueryResultNumber() {
            Object value = config.get(MAX_QUERY_RESULT_NUMBER);
            Integer number;
            if(value != null){
                if(value instanceof Integer){
                    number = (Integer) value;
                } else {
                    try {
                        number = new Integer(value.toString());
                    }catch (NumberFormatException e) {
                        return -1;
                    }
                }
            } else {
                return -1;
            }
            if(number.intValue()<=0){
                return 0;
            } else {
                return number;
            }
        }
        /**
         * Checks if the configuration is valid and throws a {@link ConfigurationException}
         * if not.<p>
         * This method checks the {@link Yard#ID} property and than calls
         * {@link #validateConfig()} to check additional constraints of specific
         * Yard configurations (subclasses of this class)
         * @return returns true if valid
         * @throws ConfigurationException
         */
        protected final boolean isValid() throws ConfigurationException{
            String id = getId();
            if(id == null || id.isEmpty()){
                throw new ConfigurationException(Yard.ID, "The ID of the Yard MUST NOT be NULL nor empty!");
            }
            validateConfig();
            return true;
        }
        /**
         * Needs to be implemented by Subclasses to check required configurations of
         * specific Yard Implementations.
         * @throws ConfigurationException In case of a missing or invalid configuration
         * for one of the properties.
         */
        protected abstract void validateConfig() throws ConfigurationException;
    }
}
