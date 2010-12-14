/**
 *
 */
package eu.iksproject.fise.servicesapi.helper.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.clerezza.rdf.core.LiteralFactory;
import org.apache.clerezza.rdf.core.NoConvertorException;
import org.apache.clerezza.rdf.core.NonLiteral;
import org.apache.clerezza.rdf.core.Resource;
import org.apache.clerezza.rdf.core.Triple;
import org.apache.clerezza.rdf.core.TypedLiteral;
import org.apache.clerezza.rdf.core.UriRef;
import org.apache.clerezza.rdf.core.impl.TripleImpl;

import eu.iksproject.fise.servicesapi.helper.Rdf;
import eu.iksproject.fise.servicesapi.helper.RdfEntity;
import eu.iksproject.fise.servicesapi.rdf.Properties;

public class RdfProxyInvocationHandler implements InvocationHandler {

    /**
     * The getID method of the RdfEntity Interface
     */
    protected static final Method getIDMethod;

    /**
     * The toString Method of {@link Object}
     */
    protected static final Method toString;

    /**
     * The equals Method of {@link Object}
     */
    protected static final Method equals;

    /**
     * The hashCode Method of {@link Object}
     */
    protected static final Method hashCode;

    static {
        try {
            getIDMethod = RdfEntity.class.getMethod("getId", new Class<?>[]{});
            toString = Object.class.getMethod("toString", new Class<?>[]{});
            equals = Object.class.getMethod("equals", new Class<?>[]{Object.class});
            hashCode = Object.class.getMethod("hashCode", new Class<?>[]{});
        } catch (SecurityException e) {
            throw new IllegalStateException("Unable to access getId Method in the "+RdfEntity.class+" Interface",e);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Unable to find getId Method in the "+RdfEntity.class+" Interface",e);
        }
    }

    /**
     * The logger TODO: Question: How to get the dependencies for logging working with maven :(
     */
//    private static final Logger log = LoggerFactory.getLogger(RdfProxyInvocationHandler.class);

    protected SimpleRdfEntityFactory factory;
    protected LiteralFactory literalFactory;
    protected NonLiteral rdfNode;
    private final Set<Class<?>> interfaces;
    public RdfProxyInvocationHandler(SimpleRdfEntityFactory factory, NonLiteral rdfNode, Class<?>[] parsedInterfaces, LiteralFactory literalFactory){
        this.rdfNode = rdfNode;
        this.factory = factory;
        this.literalFactory = literalFactory;
        //TODO If slow implement this by directly using the MGraph Interface!
        Collection<UriRef> nodeTypes = getValues(Properties.RDF_TYPE, UriRef.class);
        Set<Class<?>> interfaceSet = new HashSet<Class<?>>();
        for (Class<?> clazz : parsedInterfaces){
            if(!clazz.isInterface()){
                throw new IllegalStateException("Parsed Class "+clazz+" is not an interface!");
            }
            interfaceSet.add(clazz);
            getSuperInterfaces(clazz, interfaceSet);
        }
        this.interfaces = Collections.unmodifiableSet(interfaceSet); //nobody should be able to change this!
        for (Class<?> clazz : this.interfaces){
            Rdf classAnnotation = clazz.getAnnotation(Rdf.class);
            if(classAnnotation == null){
            } else { //check of the type statement is present
                UriRef typeRef = new UriRef(classAnnotation.id());
                if(!nodeTypes.contains(typeRef)){
                    //TODO: Question: How to get the dependencies for logging working with maven :(
                    //log.debug("add type "+typeRef+" for interface "+clazz+" to node "+rdfNode);
                    addValue(Properties.RDF_TYPE, typeRef); //add the missing type!
                } else {
                    // else the type is already present ... nothing to do
                    //TODO: Question: How to get the dependencies for logging working with maven :(
                    //log.debug("type "+typeRef+" for interface "+clazz+" is already present for node "+rdfNode);
                }
            }
        }
    }

    private static void getSuperInterfaces(Class<?> interfaze, Collection<Class<?>> interfaces){
        for (Class<?> superInterface : interfaze.getInterfaces()){
            if(superInterface != null){
                interfaces.add(superInterface);
                getSuperInterfaces(superInterface, interfaces); //recursive
            }
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //RdfEntity rdfEntity;
        if(!(proxy instanceof RdfEntity)){
            throw new IllegalArgumentException("Parsed proxy instance is not of type "+RdfEntity.class
                    +". This RdfWrapperInvocationHandler implementations only work for proxies implementing this interface!");
        }
        //First check for Methods defined in RDFEntity and java.lang.Object
        //implementation of the RffEntity Interface method!
        if(method.equals(getIDMethod)){
            return rdfNode;
        }
        //implement toString
        if(method.equals(equals)){
            return args[0] != null && args[0] instanceof RdfEntity && ((RdfEntity)args[0]).getId().equals(rdfNode);
        }
        //implement hashCode
        if(method.equals(hashCode)){
            return rdfNode.toString().hashCode();
        }
        //implement toString
        if(method.equals(toString)){
            return "Proxy for Node "+rdfNode+" and interfaces "+interfaces;
        }
        Rdf rdf = method.getAnnotation(Rdf.class);
        if(rdf == null){
            throw new IllegalStateException("Invoked Method does not have an Rdf annotation!");
        }
        UriRef property;
        if(rdf.id().startsWith("http://") || rdf.id().startsWith("urn:")){
            property = new UriRef(rdf.id());
        } else {
            throw new IllegalStateException("The id=\""+rdf.id()+"\"provided by the rdf annotation is not an valid URI");
        }
        //check for Write (Setter) Method
        if(method.getReturnType().equals(void.class)){
            Type[] parameterTypes = method.getGenericParameterTypes();
            //check the parameter types to improve error messages!
            //Only methods with a single parameter are supported
            if(parameterTypes.length!=1){
                throw new IllegalStateException("Unsupported parameters for Method "+method.toString()
                        +"! Only setter methodes with a singe parameter are supported.");
            }
            final Type parameterType = parameterTypes[0];
            //now check if args != null and has an element
            if(args == null){
                throw new IllegalArgumentException(
                        "NULL parsed as \"Object[] args\". An array with a single value is expected when calling "+method.toString()+"!");
            }
            if(args.length<1){
                throw new IllegalArgumentException(
                        "An empty array was parsed as \"Object[] args\". An array with a single value is expected when calling method "+method.toString()+"!");
            }
            final Object value = args[0];
            //Handle Arrays
            if(parameterType instanceof Class<?> && ((Class<?>)parameterType).isArray()){
                throw new IllegalStateException("No support for Arrays right now. Use "+Collection.class+" instead");
            }
            //if null is parsed as value we need to delete all values
            if (value == null){
                removeValues(property);
                return null; //setter methods are void -> return null
            }
            //if a collection is parsed we need to check the generic type
            if (Collection.class.isAssignableFrom(value.getClass())){
                Type genericType = null;
                if (parameterTypes[0] instanceof ParameterizedType){
                    for(Type typeArgument : ((ParameterizedType)parameterTypes[0]).getActualTypeArguments()){
                        if (genericType == null){
                            genericType = typeArgument;
                        } else {
                            //TODO: replace with a warning but for testing start with an exception
                            throw new IllegalStateException(
                                    "Multiple generic type definition for method "+method.toString()
                                    +" (generic types: "+((ParameterizedType) parameterTypes[0]).getActualTypeArguments()+")");
                        }
                    }
                }
                setValues(property, (Collection<?>) value);
                return null;
            } else {
                setValue(property, value);
                return null;
            }
        } else { //assume an read (getter) method
            Class<?> returnType = method.getReturnType();
            if (Collection.class.isAssignableFrom(returnType)){
                Type genericType = null;
                Type genericReturnType = method.getGenericReturnType();
                if (genericReturnType instanceof ParameterizedType){
                    ParameterizedType type = (ParameterizedType) genericReturnType;
                    for (Type typeArgument : type.getActualTypeArguments()){
                        if (genericType == null){
                            genericType = typeArgument;
                        } else {
                            //TODO: replace with a warning but for testing start with an exception
                            throw new IllegalStateException(
                                    "Multiple generic type definition for method "+method.toString()
                                    +" (generic types: "+type.getActualTypeArguments()+")");
                        }
                    }
                }
                if (genericType == null){
                    throw new IllegalStateException(
                            "Generic Type not defined for Collection in Method "+method.toString()
                            +" (generic type is needed to correctly map rdf values for property "+property);
                }
                return getValues(property, (Class<?>)genericType);
            } else {
                return getValue(property, returnType);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T getValue(UriRef property, Class<T> type){
        Iterator<Triple> results = factory.getGraph().filter(rdfNode, property, null);
        if (results.hasNext()){
            Resource result = results.next().getObject();
            if (result instanceof NonLiteral){
                if (RdfEntity.class.isAssignableFrom(type)){
                    return (T)factory.getProxy((NonLiteral)result, (Class<? extends RdfEntity>)type);
                } else { //check result for UriRef and types UriRef, URI or URL
                    if(result instanceof UriRef){
                        if (UriRef.class.isAssignableFrom(type)){
                            return (T)result;
                        } else if (URI.class.isAssignableFrom(type)){
                            try {
                                return (T)new URI(((UriRef)result).getUnicodeString());
                            } catch (URISyntaxException e) {
                                throw new IllegalStateException("Unable to parse "+URI.class
                                        +" for "+UriRef.class+" value="+((UriRef)result).getUnicodeString());
                            }
                        } else if (URL.class.isAssignableFrom(type)){
                            try {
                                return (T)new URL(((UriRef)result).getUnicodeString());
                            } catch (MalformedURLException e) {
                                throw new IllegalStateException("Unable to parse "+URL.class
                                        +" for "+UriRef.class+" value="+((UriRef)result).getUnicodeString());
                            }
                        } else {
                            throw new IllegalArgumentException("Parsed Type "+type
                                    +" is not compatible for result type "+result.getClass()
                                    +" (value "+result+") of node "+rdfNode+" and property "+property
                                    +"! (Subclass of RdfEntity, UriRef, URI or URL is expected for NonLiteral Values)");
                        }
                    } else {
                        throw new IllegalArgumentException("Parsed Type "+type
                                +" is not compatible for result type "+result.getClass()
                                +" (value "+result+") of node "+rdfNode+" and property "+property
                                +"! (Subclass of RdfEntity expected as type for NonLiteral values that are no instanceof UriRef)");
                    }
                }
            } else {
                return literalFactory.createObject(type,(TypedLiteral) result);
            }
        } else {
            return null;
        }
    }
    private <T> Collection<T> getValues(UriRef property, Class<T> type){
        return new RdfProxyPropertyCollection<T>(property, type);
    }
    private void setValue(UriRef property, Object value){
        removeValues(property);
        addValue(property, value);
    }
    private void setValues(UriRef property, Collection<?> values){
        removeValues(property);
        for(Object value : values){
            addValue(property, value);
        }
    }
    protected Resource getRdfResource(Object value) throws NoConvertorException{
        if(value instanceof Resource){
            //if the parsed object is already a Resource
            return (Resource) value; //return it
        } else if(value instanceof RdfEntity){ //check for other proxies
            return ((RdfEntity)value).getId();
        } else if(value instanceof URI){ //or URI links
            return new UriRef(value.toString());
        } else if(value instanceof URL){ //or URL links
            return new UriRef(value.toString());
        } else { //nothing of that
            //try to make an Literal (Clarezza internal Adapters)
            return literalFactory.createTypedLiteral(value);
        }
    }
    private boolean addValue(UriRef property, Object value){
        Resource rdfValue;
        try {
            rdfValue = getRdfResource(value);
            return factory.getGraph().add(new TripleImpl(rdfNode, property, rdfValue));
        } catch (NoConvertorException e){
            throw new IllegalArgumentException("Unable to transform "+value.getClass()
                    +" to an RDF Node. Only "+RdfEntity.class+" and RDF Literal Types are supported");
        }
    }
    private boolean removeValue(UriRef property, Object value){
        Resource rdfValue;
        try {
            rdfValue = getRdfResource(value);
            return factory.getGraph().remove(new TripleImpl(rdfNode, property, rdfValue));
        } catch (NoConvertorException e){
            throw new IllegalArgumentException("Unable to transform "+value.getClass()
                    +" to an RDF Node. Only "+RdfEntity.class+" and RDF Literal Types are supported");
        }
    }
    private void removeValues(UriRef proptery){
        Iterator<Triple> toRemove = factory.getGraph().filter(rdfNode, proptery, null);
        while(toRemove.hasNext()){
            factory.getGraph().remove(toRemove.next());
        }
    }

    /**
     * We need this class to apply changes in the collection to the MGraph.
     * This collection implementation is a stateless wrapper over the
     * triples selected by the subject,property pair over the MGraph!<br>
     * Default implementation of {@link AbstractCollection} are very poor
     * performance. Because of that this class overrides some methods
     * already implemented by its abstract super class.
     * @author westei
     *
     * @param <T>
     */
    private class RdfProxyPropertyCollection<T> extends AbstractCollection<T> {

        //private final NonLiteral resource;
        private final UriRef property;
        private final Class<T> genericType;
        private final boolean entity;
        private final boolean uri;
        private final boolean url;
        private final boolean uriRef;

        private RdfProxyPropertyCollection(UriRef property,Class<T> genericType) {
            this.property = property;
            this.genericType = genericType;
            entity = RdfEntity.class.isAssignableFrom(genericType);
            uri = URI.class.isAssignableFrom(genericType);
            url = URL.class.isAssignableFrom(genericType);
            uriRef = UriRef.class.isAssignableFrom(genericType);
        }

        @Override
        public Iterator<T> iterator() {
            return new Iterator<T>() {
                Iterator<Triple> results = factory.getGraph().filter(rdfNode, property, null);
                @Override
                public boolean hasNext() {
                    return results.hasNext();
                }

                @SuppressWarnings("unchecked")
                @Override
                public T next() {
                    Resource value = results.next().getObject();
                    if (entity){
                        //type checks are done within the constructor
                        return (T) factory.getProxy((NonLiteral)value, (Class<? extends RdfEntity>)genericType);
                    } else if(uri){
                        try {
                            return (T)new URI(((UriRef)value).getUnicodeString());
                        } catch (URISyntaxException e) {
                            throw new IllegalStateException("Unable to parse "+URI.class+" for "+UriRef.class+" value="+((UriRef)value).getUnicodeString());
                        }
                    } else if(url){
                        try {
                            return (T)new URL(((UriRef)value).getUnicodeString());
                        } catch (MalformedURLException e) {
                            throw new IllegalStateException("Unable to parse "+URL.class+" for "+UriRef.class+" value="+((UriRef)value).getUnicodeString());
                        }
                    } else if(uriRef){
                        return (T)value;
                    } else {
                        return literalFactory.createObject(genericType, (TypedLiteral)value);
                    }
                }

                @Override
                public void remove() {
                    results.remove(); //no Idea if Clerezza implements that ^
                }
            };
        }

        @Override
        public int size() {
            Iterator<Triple> results = factory.getGraph().filter(rdfNode, property, null);
            int size = 0;
            for (;results.hasNext();size++){
                results.next();
            }
            return size;
        }
        public boolean add(T value) {
            return addValue(property, value);
        }
        @Override
        public boolean remove(Object value) {
            return removeValue(property,value);
        }
        @Override
        public boolean isEmpty() {
            return !factory.getGraph().filter(rdfNode, property, null).hasNext();
        }
    }
}
