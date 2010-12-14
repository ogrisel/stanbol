package eu.iksproject.rick.yard.solr.defaults;


/**
 * Defines the defaults used to encode the fields for the index.<p>
 * The configuration provided by this class MUST be in agreement with the
 * schema.xml configured for the Solr Server
 * @author Rupert Westenthaler
 *
 */
public class SolrConst {
    /**
     * Char used to mark special fields. Special fields are internally used
     * fields that do not represent a value that was present in the original
     * resource. They are used to store configurations, to collect values of
     * different fields (e.g. labels with different languages)
     */
    public static final char SPECIAL_FIELD_PREFIX = '_';
    /**
     * The Char used to separate prefix, path elements and suffix.
     * Currently there is no support for escaping path elements. Therefore
     * there is only the possibility to use '/' or '#' because they do not
     * appear in prefixes or suffixes and are replaced by prefixes for the
     * path elements.
     */
    public static final char PATH_SEPERATOR = '/';
    /**
     * All fields indicating a language start with this character.
     * Special fields that indicate a language start with the
     * {@link #SPECIAL_FIELD_PREFIX} followed by this one.<p>
     * Examples:<ul>
     * <li>@en ... for a field storing English text
     * <li>@ ... for a field storing text without a language
     *           (meaning that this text is valid in any language)
     * <li>_!@ ... for a field that index all labels in any language. This field
     *    uses the {@link #SPECIAL_FIELD_PREFIX},{@link #MERGER_INDICATOR} and
     *    the {@link #LANG_INDICATOR}
     * </ul>
     */
    public static final char LANG_INDICATOR = '@';
    /**
     * Merger Fields are fields that collect different values already indexed
     * in some other fields. This fields are usually configured as
     * <code>store=false</code> and <code>multiValue=true</code> in the index
     * and are used for queries.<p>
     * The most used merger field is the {@link #LANG_MERGER_FIELD} that contains
     * all natural language values of all languages!
     */
    public static final char MERGER_INDICATOR = '!';
    /**
     * Field that stores all natural language text values of a field -
     * regardless of the language of the text.
     */
    public static final String LANG_MERGER_FIELD = ""+SPECIAL_FIELD_PREFIX+MERGER_INDICATOR+LANG_INDICATOR;
    /**
     * The name of the field used to store the unique id of the Documents (usually
     * the URI of the resource)
     */
    public static final String DOCUMENT_ID_FIELD = "uri";
    /**
     * This is used as field name to store all URIs a document
     * refers to. If a document is deleted from the index, than all other documents
     * that refer to this URI need to be updated
     */
    public static final String REFERRED_DOCUMENT_FIELD = SPECIAL_FIELD_PREFIX+"ref";
    /**
     * This is used as field name to store all URIs a document uses in one of
     * it's paths.<p> If a document in the index is changed, than all documents
     * that are dependent on this one need to be updated.
     */
    public static final String DEPENDENT_DOCUMENT_FIELD = SPECIAL_FIELD_PREFIX+"dep";

    public static final String SPECIAL_CONFIG_FIELD = SPECIAL_FIELD_PREFIX+"config";
    /**
     * The name of the field that indicates the domain of a document.<p>
     * Within the schema.xml this field is usually configures as
     * <code>multiValued=false stored=false indexed=true</code>.
     * NOTE: that the two domains sharing the same SolrIndex MUST NOT add
     * Documents with the same ID (equal values for {@link #DOCUMENT_ID_FIELD})
     */
    public static final String DOMAIN_FIELD = SPECIAL_FIELD_PREFIX+"domain";
    /**
     * The field name used by Solr for the score of query results
     */
    public static final String SCORE_FIELD = "score";

}
