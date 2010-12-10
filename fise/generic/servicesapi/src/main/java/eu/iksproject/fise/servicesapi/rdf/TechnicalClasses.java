package eu.iksproject.fise.servicesapi.rdf;

import org.apache.clerezza.rdf.core.UriRef;

/**
 * Classes to be used as types for resources that are not real life entities but
 * technical data modeling for FISE components.
 *
 * @author ogrisel
 */
public interface TechnicalClasses {

    /**
     * Type used for all enhancement created by Fise
     */
    public static final UriRef FISE_ENHANCEMENT = new UriRef(NamespaceEnum.fise
            + "Enhancement");

    /**
     * Type used for annotations on portions of a text document. This type is
     * intended to be used in combination with FISE_ENHANCEMENT
     */
    public static final UriRef FISE_TEXT_ANNOTATION = new UriRef(
            NamespaceEnum.fise + "TextAnnotation");

    /**
     * Type used for annotating a content item with categories or topics. This
     * type is intended to be used in combination with FISE_ENHANCEMENT
     */
    public static final UriRef FISE_CATEGORY_ANNOTATION = new UriRef(
            NamespaceEnum.fise + "CategoryAnnotation");

    /**
     * Type used for annotating a content item with the most important terms, or
     * tags. This type is intended to be used in combination with
     * FISE_ENHANCEMENT
     */
    public static final UriRef FISE_TERM_ANNOTATION = new UriRef(
            NamespaceEnum.fise + "TermAnnotation");

    /**
     * Type used for annotations of named entities. This type is intended to be
     * used in combination with FISE_ENHANCEMENT
     */
    public static final UriRef FISE_ENTITY_ANNOTATION = new UriRef(
            NamespaceEnum.fise + "EntityAnnotation");

    /**
     * To be used as a type pour any semantic knowledge extraction
     */
    @Deprecated
    public static final UriRef FISE_EXTRACTION = new UriRef(
            "http://iks-project.eu/ns/fise/extraction/Extraction");

    /**
     * To be used as a complement type for extraction that are relevant only to
     * the portion of context item (i.e. a sentence, an expression, a word)
     * TODO: rwesten: Check how this standard can be used for FISE enhancements
     *
     * @deprecated
     */
    @Deprecated
    public static final UriRef ANNOTEA_ANNOTATION = new UriRef(
            "http://www.w3.org/2000/10/annotation-ns#Annotation");

    /**
     * To be used to type the URI of the content item being annotated by FISE
     */
    public static final UriRef FOAF_DOCUMENT = new UriRef(NamespaceEnum.foaf
            + "Document");

    /**
     * Used to indicate, that an EntityAnnotation describes an Categorisation.
     * see <a href=
     * "http://wiki.iks-project.eu/index.php/ZemantaEnhancementEngine#Mapping_of_Categories"
     * > Mapping of Categories</a> for more Information)
     */
    @Deprecated
    public static final UriRef FISE_CATEGORY = new UriRef(NamespaceEnum.fise
            + "Category");
}
