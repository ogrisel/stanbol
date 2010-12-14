package eu.iksproject.rick.core.model;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import eu.iksproject.rick.core.utils.ModelUtils;
import eu.iksproject.rick.core.utils.TextIterator;
import eu.iksproject.rick.core.utils.TypeSaveIterator;
import eu.iksproject.rick.servicesapi.model.Reference;
import eu.iksproject.rick.servicesapi.model.Representation;
import eu.iksproject.rick.servicesapi.model.Text;
import eu.iksproject.rick.servicesapi.model.UnsupportedTypeException;
import eu.iksproject.rick.servicesapi.model.ValueFactory;

public class InMemoryRepresentation implements Representation,Cloneable {

    //private final Logger log = LoggerFactory.getLogger(InMemoryRepresentation.class);
    private static ValueFactory valueFactory = new InMemoryValueFactory();

    protected final Map<String,Object> representation;
    protected final Map<String,Object> unmodRepresentation;
    private final String id;

    public InMemoryRepresentation(String id){
        this(id,null);
    }
    /**
     * Initialise a new InMemoryRepresenation that contains already some data.
     * @param id
     * @param representation
     */
    protected InMemoryRepresentation(String id, Map<String,Object> representation){
        if(id == null){
            throw new IllegalArgumentException("The id of a Representation instance MUST NOT be NULL!");
        }
        this.id = id;
        if(representation == null){
            this.representation = new HashMap<String, Object>();
        } else {
            this.representation = representation;
        }
        unmodRepresentation = Collections.unmodifiableMap(this.representation);
    }
    @SuppressWarnings("unchecked")
    @Override
    public void add(String field, Object parsedValue) {
        //TODO:add processing of values
        // URI, URL -> Reference
        // String[] -> Text
        // check Collections!
        // The rest should be added as Objects
        Collection<Object> newValues = new ArrayList<Object>();
        ModelUtils.checkValues(valueFactory, parsedValue, newValues);
        Object values = representation.get(field);
        if(values != null){
            if(values instanceof Collection<?>){
                ((Collection<Object>) values).addAll(newValues);
            } else {
                if(newValues.size() == 1 && values.equals(newValues.iterator().next())){
                    return; //do not create an collection of the current value equals the added
                }
                Collection<Object> collection = new HashSet<Object>();
                //reset the field to the collection
                representation.put(field, collection);
                //add the two values
                collection.add(values);
                collection.addAll(newValues);
            }
        } else {
            //also here do not add the collection if there is only one value!
            representation.put(field, newValues.size() == 1?newValues.iterator().next():newValues);
        }
    }
    protected void addValues(String field,Collection<Object> values){

    }

    @Override
    public void addNaturalText(String field, String text, String... languages) {
        if(languages == null || languages.length<1){ //if no language is parse add the default lanugage!
            add(field,valueFactory.createText(text, null));
        } else {
            for(String lang : languages){
                add(field,valueFactory.createText(text, lang));
            }
        }
    }

    @Override
    public void addReference(String field, String reference) {
        add(field, valueFactory.createReference(reference));
    }
    /**
     * Getter for the values of the field as Collections. If the field is not
     * present it returns an empty Collections!
     * @param field the field
     * @return A read only collection with the values of the field
     */
    @SuppressWarnings("unchecked")
    private Collection<Object> getValuesAsCollection(String field){
        Object value = representation.get(field);
        if(value == null){
            return Collections.emptySet();
        } else if(value instanceof Collection<?>){
            return (Collection<Object>)value;
        } else {
            return Collections.singleton(value);
        }
    }
    @Override
    public <T> Iterator<T> get(String field, Class<T> type) throws UnsupportedTypeException {
        Collection<Object> values = getValuesAsCollection(field);
        return new TypeSaveIterator<T>(values.iterator(), type);
    }

    @Override
    public Iterator<Object> get(String field) {
        return getValuesAsCollection(field).iterator();
    }
    @Override
    public Iterator<Text> getText(String field) {
        Collection<Object> values = getValuesAsCollection(field);
        return values != null?new TextIterator(valueFactory,values.iterator()):null;
    }

    @Override
    public Iterator<Text> get(String field, String... languages) {
        final Collection<Object> values = getValuesAsCollection(field);
        return new TextIterator(valueFactory,values.iterator(), languages);
    }

    @Override
    public Iterator<String> getFieldNames() {
        return unmodRepresentation.keySet().iterator();
    }

    @Override
    public <T> T getFirst(String field, Class<T> type) throws UnsupportedTypeException {
        Iterator<T> values = get(field,type);
        return values.hasNext()?values.next():null;
    }

    @Override
    public Object getFirst(String field) {
        Iterator<Object> values = get(field);
        return values.hasNext()?values.next():null;
    }

    @Override
    public Text getFirst(String field, String... languages) {
        Iterator<Text> values = get(field,languages);
        return values.hasNext()?values.next():null;
    }

    @Override
    public String getId() {
        return id;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void remove(String field, Object value) {
        Object values = representation.get(field);
        if(values == null) return;
        if(value.equals(value)){
            representation.remove(field);
        } else if(values instanceof Collection<?>){
            if(((Collection<Object>)values).remove(value) && //remove the Element
                    ((Collection<Object>)values).size()<2){ //if removed check for size
                //it only one element remaining -> replace the collection with a Object
                representation.put(field, ((Collection<Object>)values).iterator().next());
            }

        } //else ignore
    }

    @Override
    public void removeAll(String field) {
        representation.remove(field);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void removeAllNaturalText(String field, String... languages) {
        Object values = representation.get(field);
        if(values == null) return;
        if(values instanceof Collection<?>){
            int removed = 0;
            for(Iterator<Text> it = new TextIterator(valueFactory,
                    ((Collection<Object>)values).iterator(),
                    languages);it.hasNext();){
                it.next();//go to the next Element
                it.remove(); //and remove ist
                removed++;
            }
            if(removed>0){ //if some elements where removed
                //check if there is only a singe or no elements left for the field
                int size = ((Collection<Object>)values).size();
                if(size==1){
                    representation.put(field, ((Collection<Object>)values).iterator().next());
                } else if(size<1){
                    representation.remove(field);
                }
            }
        } else if(isNaturalLanguageValue(values, languages)){
            representation.remove(field);
        } //else there is a single value that does not fit -> nothing todo
    }

    @SuppressWarnings("unchecked")
    @Override
    public void removeNaturalText(String field, String text, String... languages) {
        Object values = representation.get(field);
        if(values == null) return;
        if(values instanceof Collection<?>){
            int removed = 0;
            for(Iterator<Text> it = new TextIterator(valueFactory,
                    ((Collection<Object>)values).iterator(),
                    languages);it.hasNext();){
                Text label = it.next();//go to the next element
                if(text.equals(label.getText())){
                    it.remove();//and remove it
                    removed++;
                }
            }
            if(removed>0){ //if some elements where removed
                //check if there is only a singe or no elements left for the field
                int size = ((Collection<Object>)values).size();
                if(size==1){
                    representation.put(field, ((Collection<Object>)values).iterator().next());
                } else if(size<1){
                    representation.remove(field);
                }
            }
        } else if(text.equals(getNaturalLanguageValue(values, languages))){
            representation.remove(field);
        } //else there is a single value that does not fit -> nothing todo

    }

    @Override
    public void removeReference(String field, String reference) {
        try {
            remove(field,new URI(reference));
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("parsed reference needs to be an valid URI",e);
        }
    }

    @Override
    public void set(String field, Object value) {
        representation.remove(field);
        add(field,value);

    }

    @Override
    public void setNaturalText(String field, String text, String... languages) {
        removeAllNaturalText(field, languages);
        if(text != null){
            addNaturalText(field, text, languages);
        }
    }

    @Override
    public void setReference(String field, String reference) {
        removeAll(field);
        if(reference != null){
            addReference(field, reference);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object clone() throws CloneNotSupportedException {
        Map<String,Object> clone = new HashMap<String, Object>();
        for(Entry<String,Object> e : representation.entrySet()){
            if(e.getValue() instanceof HashSet<?>){
                clone.put(e.getKey(), ((HashSet<?>)e.getValue()).clone());
            } else if(e.getValue() instanceof Collection<?>){
                HashSet<Object> valuesClone = new HashSet<Object>();
                for(Iterator<Object> it = ((Collection<Object>)e.getValue()).iterator();it.hasNext();valuesClone.add(it.next()));
                clone.put(e.getKey(), valuesClone);
            } else {
                clone.put(e.getKey(), e.getValue());
            }
        }
        return new InMemoryRepresentation(id, clone);
    }
    @Override
    public Reference getFirstReference(String field) {
        Iterator<Reference> it = getReferences(field);
        return it.hasNext()?it.next():null;
    }
    @Override
    public Iterator<Reference> getReferences(String field) {
        Collection<Object> values = getValuesAsCollection(field);
        return new TypeSaveIterator<Reference>(values.iterator(), Reference.class);
    }
    private static String getNaturalLanguageValue(Object check,Set<String> langSet,boolean isNullLanguage){
        if(check instanceof Text){
            Text text = (Text)check;
            if(langSet == null || langSet.contains(text.getLanguage())){
                return text.getText();
            } // else empty arrey -> filter
        } else if(isNullLanguage && check instanceof String){
            return (String)check;
        } //type does not fit -> ignore
        return null; //no label found
    }
    public static String getNaturalLanguageValue(Object check,String...languages){
        Set<String> langSet;
        boolean isNullLanguage;
        if(languages != null && languages.length>1){
            langSet = new HashSet<String>(Arrays.asList(languages));
            isNullLanguage = langSet.contains(null);
        } else {
            langSet = null;
            isNullLanguage = true;
        }
        return getNaturalLanguageValue(check,langSet,isNullLanguage);
    }
    /**
     * @param check
     * @param languages
     * @return
     */
    public static boolean isNaturalLanguageValue(Object check,String...languages){
        return getNaturalLanguageValue(check,languages) != null;
    }
    @Override
    public String toString() {
        return InMemoryRepresentation.class.getSimpleName()+getId();
    }
    @Override
    public int hashCode() {
        return getId().hashCode();
    }
    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof Representation && ((Representation)obj).getId().equals(getId());
    }
}
