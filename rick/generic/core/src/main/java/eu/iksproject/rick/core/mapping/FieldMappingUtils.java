package eu.iksproject.rick.core.mapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.iksproject.rick.core.utils.ModelUtils;
import eu.iksproject.rick.servicesapi.defaults.DataTypeEnum;
import eu.iksproject.rick.servicesapi.defaults.NamespaceEnum;
import eu.iksproject.rick.servicesapi.mapping.FieldMapper;
import eu.iksproject.rick.servicesapi.mapping.FieldMapping;
import eu.iksproject.rick.servicesapi.query.Constraint;
import eu.iksproject.rick.servicesapi.query.TextConstraint;
import eu.iksproject.rick.servicesapi.query.ValueConstraint;
import eu.iksproject.rick.servicesapi.query.Constraint.ConstraintType;
import eu.iksproject.rick.servicesapi.query.TextConstraint.PatternType;

public class FieldMappingUtils {
    /**
     * Comparator that sorts field mappings in a way that optimises the
     * processing.
     */
    public static final FieldMappingComparator FIELD_MAPPING_COMPARATOR = new FieldMappingComparator();

    /**
     * Sorts FieldMappings by the following order:
     * <ol>
     * <li> mappings that use no wildcard are ranked first than
     * <li> mappings that ignore Fields ({@link FieldMapping#ignoreField()}=true)
     * </ol>
     * @author Rupert Westenthaler
     *
     */
    public static class FieldMappingComparator implements Comparator<FieldMapping> {

        @Override
        public int compare(FieldMapping fm17, FieldMapping fm33) {
            //in my companies QM 9000 system
            //  ... fm17 stands for critical deviation and
            //  ... fm33 stands for suggestion for improvement
            // and the nested in-line if are good for code quality!
            //   ... sorry for the comments ^^
            return fm17.usesWildcard() == fm33.usesWildcard()? //both same Wildcard
                fm17.ignoreField() == fm17.ignoreField()? // both same ignore state
                    fm33.getFieldPattern().length()-fm17.getFieldPattern().length(): //longer field pattern
                        fm17.ignoreField()?-1:1: //that with ignore field=true
                    !fm17.usesWildcard()?-1:1; //that without wildcard
        }

    }

    protected final static Logger log = LoggerFactory.getLogger(FieldMappingUtils.class);
    private FieldMappingUtils() { /* Do not create Instances of Util Classes*/ }

    /**
     * Parses fieldMappings from a String formated like follows
     * <code><pre>
     *    fieldPattern &gt; mapping_1 mapping_2 ... mapping_n
     * </pre></code>
     * Parsing is done like follows:
     * <ul>
     * <li> The elements of the parsed string are split by spaces. Leading and
     *      tailing spaces are ignored.
     * <li> the <code>fieldPattern</code> supports {@link PatternType#wildcard}.
     *      '*' and '?' within this part are interpreted accordingly
     * <li> Each mapping can have an optional Filter. The filter section starts with
     *      <code>" | "</code> and ends with the next space.<br>
     *      Currently two types of Filters are supported.<br>
     *      <b>Language Filter:</b> Syntax:<code>@=&lt;lang-1&gt;,&lt;lang-2&gt;,
     *      ... ,&lt;lang-n&gt;</code>. <br>The default language can be activated by
     *      using an empty String (e.g. <code> "@=en,,de"</code>) or null
     *      (e.g.<code>"@=en,null,de</code>).<br>
     *      <b>Data Type Filter:</b> Syntax:<code>d=&lt;type-1&gt;,&lt;type-2&gt;,
     *      ... ,&lt;type-n&gt;</code>. Types can be specified by the full URI
     *      however the preferred way is to use the prefix and the local name
     *      (e.g.to allow all kind of floating point values one could use a
     *      filter like <code>"d=xsd:decimal,xsd:float,xsd:double"</code>).
     * <li> If the field should be mapped to one or more other fields, than the
     *      second element of the field MUST BE equals to <code>'&gt'</code>
     * <li> If the second element equals to '&gt', than all further Elements are
     *      interpreted as mapping target by field names that match the
     *      FieldPattern define in the first element.
     * </ul>
     * Examples:
     * <ul>
     * <li> To copy all fields define the Mapping<br>
     *      <code><pre>*</pre></code>
     * <li> This pattern copy all fields of the foaf namespace<br>
     *      <code><pre>http://xmlns.com/foaf/0.1/*</pre></code>
     * <li> The following Pattern uses the values of the foaf:name field as
     *      RICK symbol label<br>
     *      <code><pre>http://xmlns.com/foaf/0.1/name &gt; http://www.iks-project.eu/ontology/rick/model/label</pre></code>
     * </ul>
     * Notes:
     * <ul>
     * <li> The combination of patterns for the source field and the definition of
     *      mappings is possible, but typically results in situations where all
     *      field names matched by the defined pattern are copied as values of the
     *      mapped field.
     * </ul>
     * TODO: Add Support for {@link Constraint}s on the field values.
     * @param mapping The mapping
     * @return the parsed {@link FieldMapping} or <code>null</code> if the parsed
     *    String can not be parsed.
     */
    public static FieldMapping parseFieldMapping(String mapping){
        if(mapping == null || mapping.isEmpty()){
            return null;
        }
        final boolean ignore = mapping.charAt(0) == '!';
        if(ignore){
            mapping = mapping.substring(1);
        }
        //if we have the Filter separator at pos(0), than add the space that is
        //needed by the split(" ") used to get the parts.
        if(mapping.charAt(0) == '|'){
            //thats because the Apache Felix Webconsole likes to call trim and
            //users do like to ignore (the silly) required of leading spaces ...
            mapping = ' '+mapping;
        }

        String[] parts = mapping.split(" "); //TODO: maybe we should not use the spaces here
        List<String> mappedTo = Collections.emptyList();
        String fieldPattern = NamespaceEnum.getFullName(parts[0]);
        Constraint filter = null;
        for(int i=1;i<parts.length;i++){
            if("|".equals(parts[i]) && parts.length > i+1){
                filter = parseConstraint(parts[i+1]);
            }
            if(">".equals(parts[i]) && parts.length > i+1){
                mappedTo = parseMappings(parts,i+1);
            }
        }
        if(ignore && filter != null){
            log.warn(String.format("Filters are not supported for '!<fieldPatter>' type field mappings! Filter %s ignored",filter));
            filter = null;
        }
        return new FieldMapping(fieldPattern, filter, mappedTo.toArray(new String[mappedTo.size()]));
    }
//moved to NamespaceEnum
//    private static String getFullUri(String value){
//        int index = value.indexOf(':');
//        if(index>0){
//            NamespaceEnum namespace = NamespaceEnum.forPrefix(value.substring(0, index));
//            if(namespace!= null){
//                value = namespace.getNamespace()+value.substring(index+1);
//            }
//        }
//        return value;
//    }

    private static List<String> parseMappings(String[] parts, int start) {
        ArrayList<String> mappings = new ArrayList<String>(parts.length-start);
        for(int i=start;i<parts.length;i++){
            String act = NamespaceEnum.getFullName(parts[i]);
            if(!act.isEmpty()){ //needed to remove two spaces in a row
                mappings.add(act);
            }
        }
        return mappings;
    }

    private static Constraint parseConstraint(String filterString) {
        if(filterString.startsWith("d=")){
            String[] dataTypeStrings = filterString.substring(2).split(";");
            Set<String> dataTypes = new HashSet<String>();
            for(int i=0;i<dataTypeStrings.length;i++){
                DataTypeEnum dataType = DataTypeEnum.getDataTypeByShortName(dataTypeStrings[i]);
                if(dataType == null){
                    dataType = DataTypeEnum.getDataType(dataTypeStrings[i]);
                }
                if(dataType != null){
                    dataTypes.add(dataType.getUri());
                } else {
                    log.warn(String.format("DataType %s not supported! Datatype get not used by this Filter"));
                }
            }
            if(dataTypes.isEmpty()){
                log.warn(String.format("Unable to parse a valied data type form \"%s\"! A data type filter MUST define at least a single dataType. No filter will be used.",
                        filterString));
                return null;
            } else {
                return new ValueConstraint(null, dataTypes);
            }
        } else if (filterString.startsWith("@=")){
            String[] langs = filterString.substring(2).split(";");
            for(int i =0;i<langs.length;i++){
                if(langs[i].length()<1 || "null".equals(langs[i])){
                    langs[i] = null;
                }
            }
            if(langs.length<1){
                log.warn("Unable to parse a language form \"%s\"! A language filter MUST define at least a singel language. No filter will be used."+filterString);
                return null;
            } else {
                return new TextConstraint(null,langs);
            }
        } else {
            log.warn(String.format("Filters need to start with \"p=\" (dataType) or \"@=\" (language). Parsed filter: \"%s\".",filterString));
            return null;
        }
    }
    public static String[] serialiseFieldMapper(FieldMapper mapper){
        if(mapper == null){
            return null;
        } else {
            Collection<FieldMapping> mappings = mapper.getMappings();
            String[] mappingStrings = new String[mappings.size()];
            int index=0;
            for(Iterator<FieldMapping> it = mappings.iterator();it.hasNext();index++){
                mappingStrings[index] = serialiseFieldMapping(it.next());
            }
            return mappingStrings;
        }
    }

    public static String serialiseFieldMapping(FieldMapping mapping){
        if(mapping == null){
            return null;
        }
        StringBuilder builder = new StringBuilder();
        //first add the ! if we have an ignore mapping
        if(mapping.ignoreField()){
            builder.append('!');
        }
        //now the pattern (not present if global)
        if(!mapping.isGlobal()){
            String pattern = mapping.getFieldPattern();
            appendUri(builder, pattern);
        }
        builder.append(' ');
        //now the constraint!
        if(mapping.getFilter() != null){
            if(mapping.getFilter().getType() == ConstraintType.text){
                serializeConstraint(builder,(TextConstraint)mapping.getFilter());
            } else if(mapping.getFilter().getType() == ConstraintType.value){
                serializeConstraint(builder,(ValueConstraint)mapping.getFilter());
            } else {
                throw new IllegalStateException(String.format("Constraints of type %s are not supported! Please adapt this implementation!",mapping.getFilter().getType()));
            }
            builder.append(' ');
        }
        //now the mapping
        boolean first = true;
        for(String fieldMapping : mapping.getMappings()){
            if(fieldMapping != null){
                if(first){
                    builder.append(" > ");
                    first = false;
                } else {
                    builder.append(';');
                }
                appendUri(builder, fieldMapping);
            } //else default 1:1 mapping ... nothing to add
        }
        return builder.toString();
    }
    private static void serializeConstraint(StringBuilder builder,TextConstraint constraint){
        builder.append("| @=");
        boolean first = true;
        for(String lang : constraint.getLanguages()){
            if(first){
                first = false;
            } else {
                builder.append(';');
            }
            builder.append(lang);
        }
    }
    private static void serializeConstraint(StringBuilder builder,ValueConstraint constraint){
        builder.append("| d=");
        boolean first = true;
        for(String type : constraint.getDataTypes()){
            if(first){
                first = false;
            } else {
                builder.append(';');
            }
            appendUri(builder,type);
        }
    }
//    public static void main(String[] args) {
//        String test = "foaf:*";
//        System.out.println(parseFieldMapping(" | @=;de"));
//    }

    /**
     * Appends an URI if possible by using prefix:localName
     * @param builder the StringBuilder to add the URI MUST NOT be NULL
     * @param uri the URI to add MUST NOT be NULL
     */
    private static void appendUri(StringBuilder builder, String uri) {
        String[] namespaceLocal = ModelUtils.getNamespaceLocalName(uri);
        if(namespaceLocal[0]!=null){
            NamespaceEnum namespace = NamespaceEnum.forNamespace(namespaceLocal[0]);
            if(namespace != null){
                builder.append(namespace.getPrefix()).append(':');
            } else {
                builder.append(namespaceLocal[0]);
            }
        } //else no name space to add (e.g. if the pattern is "*")
        builder.append(namespaceLocal[1]);
    }
}
