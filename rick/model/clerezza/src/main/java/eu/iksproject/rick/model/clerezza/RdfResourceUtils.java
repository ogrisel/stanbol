package eu.iksproject.rick.model.clerezza;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.clerezza.rdf.core.Language;
import org.apache.clerezza.rdf.core.Literal;
import org.apache.clerezza.rdf.core.LiteralFactory;
import org.apache.clerezza.rdf.core.PlainLiteral;
import org.apache.clerezza.rdf.core.Resource;
import org.apache.clerezza.rdf.core.TypedLiteral;
import org.apache.clerezza.rdf.core.UriRef;
import org.apache.clerezza.rdf.core.impl.PlainLiteralImpl;
import org.apache.clerezza.rdf.core.impl.SimpleLiteralFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.iksproject.rick.servicesapi.defaults.DataTypeEnum;

/**
 * Utilities to create {@link Resource} instances for Java Objects.
 * @author Rupert Westenthaler
 *
 */
public class RdfResourceUtils {

    private RdfResourceUtils(){/*do not create instances of Util Classes*/}
    /**
     * Defines Mappings for the DataTypes supported by the Clerezza {@link SimpleLiteralFactory}.
     * If a xsd data type is mapped to <code>null</code> the string representation
     * should be returnd as String (bypassing the {@link LiteralFactory})
     * TODO Replace this code with our own implementation of the {@link LiteralFactory}
     *      and implement mappings for all DataTypes in {@link DataTypeEnum}
     *      (Rupert Westenthaler, 2010-11-12)
     * @author Rupert Westenthaler
     */
    public static enum XsdDataTypeEnum {
        //NOTE: Commented lines are not supported by org.apache.clerezza.rdf.core.impl.SimpleLiteralFactory
        // see http://svn.apache.org/repos/asf/incubator/clerezza/trunk/org.apache.clerezza.parent/org.apache.clerezza.rdf.core/src/main/java/org/apache/clerezza/rdf/core/impl/SimpleLiteralFactory.java
        Boolean("boolean",Boolean.class),
        //Decimal("decimeal"),
        Integer("integer",Long.class),
        Int("int",Integer.class),
        Short("short",Integer.class),
        Byte("byte",Integer.class),
        //Float("float",Double.class),
        Double("double",Double.class),
        Base64Binary("base64Binary",new byte[0].getClass()),
        DateTime("dateTime",Date.class),
        //Date("date"),
        //Time("time"),
        AnyUri("anyUri",null),
        //QName("qName"),
        //Duration("duration"),
        //GYearMonth("gYearMonth"),
        //GYear("gYear"),
        //GMonthDay("gMonthDay"),
        //GDay("gDay"),
        //GMonth("gMonth"),
        //hexBinary("hexBinary"),
        //NOTATION("NOTATION"),
        String("string",String.class),
        ;
        private static final String ns = "http://www.w3.org/2001/XMLSchema#";
        private String uri;
        private Class<?> clazz;
        XsdDataTypeEnum(String localName,Class<?> clazz){
            this.uri = ns+localName;
            this.clazz = clazz;
        }
        public String getUri(){
            return uri;
        }
        @Override
        public String toString() {
            return uri;
        }
        public Class<?> getMappedClass(){
            return clazz;
        }
    }

    /**
     * Unmodifiable map containing the supported xsd data type mappings as defined
     * by the {@link XsdDataTypeEnum}.
     */

    public static final Map<UriRef, XsdDataTypeEnum> XSD_DATATYPE_VALUE_MAPPING;
    /**
     * Unmodifiable containing all xsd data types that can be converted to
     * {@link Text} (without language).
     */
    public static final Set<UriRef> STRING_DATATYPES;

    public static final Map<Class<?>, XsdDataTypeEnum> JAVA_OBJECT_XSD_DATATYPE_MAPPING;
    static {
        Map<UriRef,XsdDataTypeEnum> dataTypeMappings = new HashMap<UriRef, XsdDataTypeEnum>();
        Map<Class<?>,XsdDataTypeEnum> objectMappings = new HashMap<Class<?>, XsdDataTypeEnum>();
        Set<UriRef> stringDataTypes = new HashSet<UriRef>();
        stringDataTypes.add(null);//map missing dataTypes to String
        for(XsdDataTypeEnum mapping : XsdDataTypeEnum.values()){
            UriRef uri = new UriRef(mapping.getUri());
            dataTypeMappings.put(uri,mapping);
            if(mapping.getMappedClass() != null && String.class.isAssignableFrom(mapping.getMappedClass())){
                stringDataTypes.add(uri);
            }
            if(mapping.getMappedClass() != null){
                objectMappings.put(mapping.getMappedClass(), mapping);
            }
        }
        XSD_DATATYPE_VALUE_MAPPING = Collections.unmodifiableMap(dataTypeMappings);
        STRING_DATATYPES = Collections.unmodifiableSet(stringDataTypes);
        JAVA_OBJECT_XSD_DATATYPE_MAPPING = Collections.unmodifiableMap(objectMappings);
    }

    private static final Logger log = LoggerFactory.getLogger(RdfResourceUtils.class);

    private static final LiteralFactory literalFactory = LiteralFactory.getInstance();

    /**
     * Creates a {@link Language} instance based on the parsed language string.
     *
     * @param lang the language (<code>null</code> is supported)
     * @return the {@link Language} or <code>null</code> if <code>null</code>
     * was parsed as language.
     */
    public static Language getLanguage(String lang) {
        final Language parsedLanguage;
        if(lang != null && lang.length()>0){
            parsedLanguage = new Language(lang);
        } else {
            parsedLanguage = null;
        }
        return parsedLanguage;
    }

    /**
     * Extracts the literal values for {@link Literal} instances.
     *
     * @param literals the Iterator holding the literals
     * @return The collection with the literal values
     */
    public static Collection<String> getLiteralValues(Iterator<Literal> literals){
        Collection<String> results = new ArrayList<String>();
        while(literals.hasNext()){
            Literal act = literals.next();
            results.add(act.getLexicalForm());
        }
        return results;
    }

    /**
     * Extracts the literal values for the given list of languages (<code>null</code>
     * is supported).
     * <p>
     * Multiple languages are supported by this method to allow parsing
     * <code>null</code> in addition to a language. This is often used by applications
     * to search for literals in a given language in addition to literals with no
     * defined language.
     * <p>
     * As a convenience this methods adds literals with a language tag to the
     * front of the list and literals with no language tag to the end.
     *
     * @param literals the iterator over the literals
     * @param languages the array of languages (<code>null</code> is supported).
     * @return The collection with all the literal values.
     */
    public static List<String> getLiteralValues(Iterator<Literal> literals, String... languages) {
        Set<Language> languageSet = new HashSet<Language>();//permits null element!
        for (String lang : languages) {
            languageSet.add(getLanguage(lang));
        }
        boolean containsNull = languageSet.contains(null);
        List<String> results = new ArrayList<String>();
        while (literals.hasNext()) {
            Literal act = literals.next();
            if (act instanceof PlainLiteral) {
                PlainLiteral pl = (PlainLiteral) act;
                if (languageSet.contains(pl.getLanguage())) {
                    results.add(0, pl.getLexicalForm()); //add to front
                }
            } else if (containsNull) { //add also all types Literals, because the do not define an language!
                results.add(act.getLexicalForm()); //append to the end
            }
        }
        return results;
    }

    /**
     * Extracts the unicode representation of URIs.
     *
     * @param uriRefObjects iterator over URIs
     * @return the unicode representation
     */
    public static Collection<String> getUriRefValues(Iterator<UriRef> uriRefObjects) {
        Collection<String> results = new ArrayList<String>();
        while (uriRefObjects.hasNext()) {
            results.add(uriRefObjects.next().getUnicodeString());
        }
        return results;
    }

    /**
     * Creates a {@link PlainLiteral} for the given literal value and language.
     * This method creates an instance of {@link PlainLiteralImpl}.<br>
     * TODO: It would be better to use something like the {@link LiteralFactory}
     * to create {@link PlainLiteral} instances. However it seams there is no
     * such functionality present.
     *
     * @param literalValue the value of the literal
     * @param lang the language of the literal
     * @return the Literal
     */
    public static PlainLiteral createLiteral(String literalValue, String lang) {
        Language language = (lang != null && lang.length() > 0) ? new Language(lang) : null;
        return new PlainLiteralImpl(literalValue, language);
    }

    public static TypedLiteral createLiteral(Object object) {
        return literalFactory.createTypedLiteral(object);
    }

}
