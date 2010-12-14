package eu.iksproject.rick.servicesapi.defaults;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.xml.datatype.Duration;
import javax.xml.namespace.QName;

import eu.iksproject.rick.servicesapi.model.Reference;
import eu.iksproject.rick.servicesapi.model.Text;
/**
 * Holds data types used by Rick. Uses the xsd data types where possible.
 *
 * @author Rupert Westenthaler
 *
 */
public enum DataTypeEnum {
    //Rick specific
    //Reference
    Reference(NamespaceEnum.rickModel,"ref",Reference.class),
    //Natural language Text
    Text(NamespaceEnum.rickModel,"text",Text.class),
    //xsd types
    /**
     * currently URIs are preferable mapped to {@link Reference}, because there
     * may be RDF URIs that are not valid {@link URI}s nor {@link URL}s. However
     * existing URI and URL instances are also accepted.
     */
    AnyUri("anyURI",Reference.class,URI.class,URL.class),
    Boolean("boolean",Boolean.class),
    Byte("byte",Byte.class),
    Short("short",Short.class),
    Integer("integer",BigInteger.class),
    Decimal("decimal",BigDecimal.class),
    Int("int",Integer.class),
    Long("long",Long.class),
    Float("float",Float.class),
    Double("double",Double.class),
    String("string",String.class,Text.class),
    Time("time",Date.class), //TODO: check if the XML calendar would be better
    Date("date",Date.class),
    DateTime("dateTime",Date.class),
    Duration("duration",Duration.class),
    ;
    final Class<?> javaType;
    final QName qName;
    final String shortName;
    final String uri;
    final Set<Class<?>> additional;
    DataTypeEnum(Class<?> javaType,Class<?>...additional){
        this(null,null,javaType,additional);
    }
    DataTypeEnum(String localName,Class<?> javaType,Class<?>...additional){
        this(null,localName,javaType,additional);
    }
    private DataTypeEnum(NamespaceEnum namespace,Class<?> javaType,Class<?>...additional) {
        this(namespace,null,javaType,additional);
    }
    private DataTypeEnum(NamespaceEnum namespace,String localName,Class<?> javaType,Class<?>...additional) {
        if(namespace == null){
            namespace = NamespaceEnum.xsd;
        }
        if(localName == null){
            localName = name();
        }
        if(additional != null && additional.length>0){
            this.additional = Collections.unmodifiableSet(new HashSet<Class<?>>(Arrays.asList(additional)));
        } else {
            this.additional = Collections.emptySet();
        }
        this.javaType = javaType;
        this.qName = new QName(namespace.getNamespace(), localName, namespace.getPrefix());
        //a lot of accesses will be based on the Uri and the shortName.
        // -> so store the IDs and the shortName as local Vars.
        this.shortName = qName.getPrefix()+':'+qName.getLocalPart();
        this.uri = qName.getNamespaceURI()+qName.getLocalPart();
    }
    public final String getLocalName() {
        return qName.getLocalPart();
    }
    public final Class<?> getJavaType() {
        return javaType;
    }
    public Set<Class<?>> getAdditionalJavaTypes() {
        return additional;
    }
    public final NamespaceEnum getNamespace() {
        return NamespaceEnum.forNamespace(qName.getNamespaceURI());
    }
    public final String getUri(){
        return uri;
    }
    public final String getShortName(){
        return shortName;
    }
    public final QName getQName(){
        return qName;
    }
    @Override
    public String toString() {
        return getUri();
    }

    static private Map<Class<?>,Set<DataTypeEnum>> class2DataTypeMap;
    static private Map<Class<?>,Set<DataTypeEnum>> interface2DataTypeMap;
    static private Map<Class<?>,Set<DataTypeEnum>> allClass2DataTypeMap;
    static private Map<Class<?>,Set<DataTypeEnum>> allInterface2DataTypeMap;
    static private Map<String,DataTypeEnum> uri2DataType;
    static private Map<String,DataTypeEnum> shortName2DataType;
    static{
        Map<Class<?>,Set<DataTypeEnum>> c2d = new HashMap<Class<?>, Set<DataTypeEnum>>();
        Map<Class<?>,Set<DataTypeEnum>> i2d = new HashMap<Class<?>, Set<DataTypeEnum>>();
        Map<Class<?>,Set<DataTypeEnum>> ac2d = new HashMap<Class<?>, Set<DataTypeEnum>>();
        Map<Class<?>,Set<DataTypeEnum>> ai2d = new HashMap<Class<?>, Set<DataTypeEnum>>();
        Map<String,DataTypeEnum> u2d = new HashMap<String, DataTypeEnum>();
        Map<String,DataTypeEnum> s2d = new HashMap<String, DataTypeEnum>();
        for(DataTypeEnum dataType : DataTypeEnum.values()){
            //add primary javaType -> data type mappings
            if(dataType.javaType.isInterface()){
                Set<DataTypeEnum> dataTypes = i2d.get(dataType.javaType);
                if(dataTypes == null){
                    dataTypes = EnumSet.noneOf(DataTypeEnum.class);
                    i2d.put(dataType.javaType, dataTypes);
                }
                dataTypes.add(dataType);
            } else { //a class
                Set<DataTypeEnum> dataTypes = c2d.get(dataType.javaType);
                if(dataTypes == null){
                    dataTypes = EnumSet.noneOf(DataTypeEnum.class);
                    c2d.put(dataType.javaType, dataTypes);
                }
                dataTypes.add(dataType);
            }
            //add additional javaType -> data type mappings
            for(Class<?> additionalClass : dataType.additional){
                if(additionalClass.isInterface()){
                    Set<DataTypeEnum> dataTypes = ai2d.get(additionalClass);
                    if(dataTypes == null){
                        dataTypes = EnumSet.noneOf(DataTypeEnum.class);
                        ai2d.put(additionalClass, dataTypes);
                    }
                    dataTypes.add(dataType);
                } else { //a class
                    Set<DataTypeEnum> dataTypes = ac2d.get(additionalClass);
                    if(dataTypes == null){
                        dataTypes = EnumSet.noneOf(DataTypeEnum.class);
                        ac2d.put(additionalClass, dataTypes);
                    }
                    dataTypes.add(dataType);
                }
            }
            if(u2d.containsKey(dataType.getUri())){
                throw new IllegalStateException(java.lang.String.format("Invalid configuration in DataTypeEnum because dataType uri %s is used for %s and %s!",
                        dataType.getUri(),dataType.name(),u2d.get(dataType.getUri()).name()));
            }
            u2d.put(dataType.getUri(), dataType);
            if(s2d.containsKey(dataType.getShortName())){
                throw new IllegalStateException(java.lang.String.format("Invalid configuration in DataTypeEnum because dataType short name (prefix:localname) %s is used for %s and %s!",
                        dataType.getShortName(),dataType.name(),s2d.get(dataType.getShortName()).name()));
            }
            s2d.put(dataType.getShortName(), dataType);
        }
        class2DataTypeMap = Collections.unmodifiableMap(c2d);
        interface2DataTypeMap = Collections.unmodifiableMap(i2d);
        allClass2DataTypeMap = Collections.unmodifiableMap(ac2d);
        allInterface2DataTypeMap = Collections.unmodifiableMap(ai2d);
        uri2DataType = Collections.unmodifiableMap(u2d);
        shortName2DataType = Collections.unmodifiableMap(s2d);
    }
    public static Set<DataTypeEnum> getPrimaryDataTypes(Class<?> javaClass){
        Set<DataTypeEnum> dataTypes = EnumSet.noneOf(DataTypeEnum.class);
        Set<DataTypeEnum> classesTypes = class2DataTypeMap.get(javaClass);
        if(classesTypes != null){
            dataTypes.addAll(classesTypes);
        }
        for(Entry<Class<?>, Set<DataTypeEnum>> entry : interface2DataTypeMap.entrySet()){
            if(entry.getKey().isAssignableFrom(javaClass)){
                dataTypes.addAll(entry.getValue());
            }
        }
        return dataTypes;

    }
    public static Set<DataTypeEnum> getAllDataTypes(Class<?> javaClass){
        //start with the primary
        Set<DataTypeEnum> all = getPrimaryDataTypes(javaClass);
        //now add the additional types
        Set<DataTypeEnum> additionalClassesTypes = allClass2DataTypeMap.get(javaClass);
        if(additionalClassesTypes != null){
            all.addAll(additionalClassesTypes);
        }
        for(Entry<Class<?>, Set<DataTypeEnum>> entry : allInterface2DataTypeMap.entrySet()){
            if(entry.getKey().isAssignableFrom(javaClass)){
                all.addAll(entry.getValue());
            }
        }
        return all;
    }
//    public static DataTypeEnum getDataType(Class<?> javaClass){
//        List<DataTypeEnum> dataTypes = getAllDataTypes(javaClass);
//        return dataTypes.isEmpty()?null:dataTypes.get(0);
//    }
    public static DataTypeEnum getDataType(String uri){
        return uri2DataType.get(uri);
    }
    public static DataTypeEnum getDataTypeByShortName(String shortName){
        return shortName2DataType.get(shortName);
    }
}
