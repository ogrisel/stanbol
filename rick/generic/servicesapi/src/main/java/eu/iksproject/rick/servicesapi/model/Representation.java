package eu.iksproject.rick.servicesapi.model;

import java.util.Iterator;

import eu.iksproject.rick.servicesapi.yard.Yard;

/**
 * This interface is used by the RICK to define representations. It is used for
 * any kind of CRUD operations on the {@link Yard} (the storage of the Rick). <br>
 * The goal if this interface is to allow implementation based on different storage
 * solutions such as CMS, full text indices, triple stores, noSQL data stores ...<br>
 *
 * TODO: handling the differences between "NaturalLanguageText", "References" and
 *       "normal" values feels to complex! Need to reevaluate if this differentiation
 *       is needed or can be done in a more easy way!
 * TODO: add an API that allows to attach Content!
 * TODO: Review this interface during the definition of a Query Language for the
 * Rick (or IKS in general)
 * TODO: Do we need subNodes or are "references" enough.
 *
 * TODO: Check to use also Wrappers for fields and values (in analogy to
 *       {@link Reference} and {@link Text}. PRO: clearer API CON: more Objects
 *       to be garbage collected.
 *
 * @author Rupert Westenthaler
 */
public interface Representation {
    /**
     * Getter for the identifier.
     * @return the identifier
     */
    String getId();
    /**
     * Getter for a single Value for a field
     * @param <T> the generic type the returned value
     * @param field the field
     * @param type the type of the values
     * @return the (first) value of that field
     * @throws IllegalArgumentException if the type is not supported
     */
    <T> T getFirst(String field,Class<T> type) throws UnsupportedTypeException;

    /**
     * Getter for all values of a field
     * @param <T> the generic type of the returned values
     * @param field the field
     * @param type the type
     * @return the values of the field
     * @throws UnsupportedTypeException if the parsed type is not supported
     */
    <T> Iterator<T> get(String field,Class<T> type) throws UnsupportedTypeException;
    /**
     * Getter for the (first) value for a field
     * @param field the field
     * @return the first value of a field
     */
    Object getFirst(String field);
    /**
     * Getter for the first reference value for a field
     * @param field the field
     * @return the reference or null of the field has no reference as value
     */
    Reference getFirstReference(String field);
    /**
     * Getter for the first natural language text value of a specific language
     * @param field the field
     * @param language the language(s) of the natural language text value
     *             (If <code>null</code> is parsed as language, than also labels
     *             without language tag are included in the Result)
     * @return the first natural language text found for the parsed field
     */
    Text getFirst(String field, String...language);
    /**
     * Getter for all values for the requested field
     * @param field the field
     * @return the values of the field
     */
    Iterator<Object> get(String field);
    /**
     * Getter for all natural language text values of a field
     * @param field the field
     * @return the natural text values
     */
    Iterator<Text> getText(String field);
    /**
     * Getter for all natural language text values of a field
     * @param field the field
     * @param language the language(s) of the natural language text value
     *             (If <code>null</code> is parsed as language, than also labels
     *             without language tag are included in the Result)
     * @return iterator over all natural language text values in the requested
     *             language.
     */
    Iterator<Text> get(String field,String...language);
    /**
     * Getter for all reference values of a field
     * @param field the field
     * @return Iterator over all reference values of a field
     */
    Iterator<Reference> getReferences(String field);
    /**
     * Adds the object as value to the field.
     * <p>The type of the value is inferred based on the type of the Object.<br>
     * Supported Types are:</p>
     * <ul>
     * <li>{@link Reference}
     * <li>URL, URI: {@link Reference} instances are created for such values</li>
     * <li>Boolean, Integer, Long, Double and Float as primitive data types</li>
     * <li>{@link Text}
     * <li>String[]{text,language}: text and language (further entries are ignored)</li>
     * <li>String: mapped to {@link Text} with the <code>language=null</code></li>
     * <li>Collection are used for adding multiple values
     * <li>For other types the toString() method is used</li>
     * </ul>
     * @param field the field
     * @param value the value to add
     */
    void add(String field, Object value);
    /**
     * Adds an reference to the field.
     * @param field the field
     * @param reference the string representation of the reference. Note that
     * the value will be interpreted as a "reference" so there might apply
     * some rules about the format of the string. Regardless of the implementation
     * any valid URI and URL need to be accepted as a valid reference value
     */
    void addReference(String field, String reference);
    /**
     * Adds a natural language text as value for one or more languages
     * @param field the field to add the text as value
     * @param text the natural language text
     * @param language the text is set for all the parsed languages. Parse
     *             <code>null</code> to set the text also without any language
     *             information.
     */
    void addNaturalText(String field,String text, String...languages);
    /**
     * Sets the value of the field to the parsed object. If the parsed value
     * is <code>null</code> than this method removes all values for the given
     * field
     * <p>The type of the value is inferred based on the type of the Object.<br>
     * Supported Types are:</p>
     * <ul>
     * <li>{@link Reference}
     * <li>URL, URI: {@link Reference} instances are created for such values</li>
     * <li>Boolean, Integer, Long, Double and Float as primitive data types</li>
     * <li>{@link Text}
     * <li>String[]{text,language}: text and language (further entries are ignored)</li>
     * <li>String: mapped to {@link Text} with the <code>language=null</code></li>
     * <li>Collection are used for adding multiple values
     * <li>For other types the toString() method is used</li>
     * </ul>
     * @param field the field
     * @param value the new value for the field
     */
    void set(String field, Object value);
    /**
     * Setter for the reference of a field. If the parsed value
     * is <code>null</code> than this method removes all values for the given
     * field.
     * @param field the field
     * @param reference the string representation of the reference. Note that
     * the value will be interpreted as a "reference" so there might apply
     * some rules about the format of the string. Regardless of the implementation
     * any valid URI and URL need to be accepted as a valid reference value
     */
    void setReference(String field, String reference);
    /**
     * Setter for the natural language text value of a field in the given
     * languages. If <code>null</code> is parsed as text, all present values
     * for the parsed languages are removed (values of other languages are
     * not removed)
     * @param field the field
     * @param text the natural language text
     * @param language the languages of the parsed text. Parse
     *             <code>null</code> to set the text also without any language
     *             information.
     */
    void setNaturalText(String field,String text, String...language);
    /**
     * Removes the parsed value form the field
     * @param field the field
     * @param value the value to remove
     */
    void remove(String field, Object value);
    /**
     * Removes to parsed reference as value for the given field.
     * @param field the field
     * @param reference the string representation of the reference. Note that
     * the value will be interpreted as a "reference" so there might apply
     * some rules about the format of the string. Regardless of the implementation
     * any valid URI and URL need to be accepted as a valid reference value
     */
    void removeReference(String field,String reference);
    /**
     * Removes a natural language text in given languages form a field
     * @param field the field
     * @param text the natural language text
     * @param language the language(s) of the natural language text
     *             (If <code>null</code> is parsed as language, than also labels
     *             without language tag might be removed)
     */
    void removeNaturalText(String field,String text,String...languages);
    /**
     * Removes all values of the field
     * @param field the field
     */
    void removeAll(String field);
    /**
     * Removes all natural language texts for the given languages
     * @param field the field
     * @param language the language(s) of the natural language text
     *             (If <code>null</code> is parsed as language, than also all labels
     *             without language tag are removed)
     */
    void removeAllNaturalText(String field,String...languages);
    /**
     * Getter for all the present fields
     * @return the fields
     */
    Iterator<String> getFieldNames();


}
