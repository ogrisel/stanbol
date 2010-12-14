package eu.iksproject.rick.query.clerezza;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import eu.iksproject.rick.core.query.FieldQueryImpl;
import eu.iksproject.rick.query.clerezza.SparqlQueryUtils.EndpointTypeEnum;
import eu.iksproject.rick.servicesapi.query.FieldQuery;
/**
 * Adds the "selected field" to "SPARQL variable name" mapping
 * @author Rupert Westenthaler
 *
 */
public class SparqlFieldQuery extends FieldQueryImpl implements FieldQuery ,Cloneable {
    /**
     * String used as prefix for variables generated for fields
     */
    private static final String FIELD_VAR_PREFIX = "v_";
    private static final String ROOT_VAR_NAME = "id";
    private int varNum;
    protected final Map<String,String> field2VarMappings;
    private final Map<String, String> unmodField2VarMappings;
    protected EndpointTypeEnum endpointType;
    protected SparqlFieldQuery() {
        this(null);
    }
    protected SparqlFieldQuery(EndpointTypeEnum endpointType){
        super();
        this.endpointType = endpointType != null?endpointType:EndpointTypeEnum.Standard;
        varNum = 0;
        field2VarMappings = new HashMap<String, String>();
        unmodField2VarMappings = Collections.unmodifiableMap(field2VarMappings);
    }
    public final EndpointTypeEnum getEndpointType() {
        return endpointType;
    }

    public final void setEndpointType(EndpointTypeEnum endpointType) {
        this.endpointType = endpointType;
    }
    /*
     * (non-Javadoc)
     * @see eu.iksproject.rick.core.query.FieldQueryImpl#addSelectedField(java.lang.String)
     */
    @Override
    public void addSelectedField(String field) {
        super.addSelectedField(field);
        field2VarMappings.put(field, getFieldVar());
    }
    /*
     * (non-Javadoc)
     * @see eu.iksproject.rick.core.query.FieldQueryImpl#addSelectedFields(java.util.Collection)
     */
    @Override
    public void addSelectedFields(Collection<String> fields) {
        super.addSelectedFields(fields);
        for(String field : fields){
            field2VarMappings.put(field, getFieldVar());
        }
    }
    /*
     * (non-Javadoc)
     * @see eu.iksproject.rick.core.query.FieldQueryImpl#removeSelectedField(java.lang.String)
     */
    @Override
    public void removeSelectedField(String field) {
        super.removeSelectedField(field);
        field2VarMappings.remove(field);
    }
    /*
     * (non-Javadoc)
     * @see eu.iksproject.rick.core.query.FieldQueryImpl#removeSelectedFields(java.util.Collection)
     */
    @Override
    public void removeSelectedFields(Collection<String> fields) {
        super.removeSelectedFields(fields);
        for(String field : fields){
            field2VarMappings.remove(field);
        }
    }
    /**
     * Getter for the variable name for a selected field
     * @param field the selected field
     * @return the variable name or <code>null</code> if the parsed field is not selected.
     */
    public String getVariableName(String field){
        return field2VarMappings.get(field);
    }
    /**
     * Getter for the unmodifiable field name to variable name mapping.
     * @return
     */
    public Map<String,String> getFieldVariableMappings(){
        return unmodField2VarMappings;
    }
    private String getFieldVar(){
        varNum++;
        return FIELD_VAR_PREFIX+varNum;
    }
    public String getRootVariableName(){
        return ROOT_VAR_NAME;
    }
    /**
     * Clones the query (including the field to var name mapping)
     */
    @Override
    public SparqlFieldQuery clone() {
        SparqlFieldQuery clone = super.copyTo(new SparqlFieldQuery());
        //Note: this uses the public API. However the field->ar mapping might still
        //be different if any removeSelectedField(..) method was used on this
        //instance. Because of that manually set the map and the value of the int.
        //clone.field2VarMappings.clear(); //clear is not necessary, because the keys are equals!
        clone.field2VarMappings.putAll(field2VarMappings);
        clone.varNum = varNum;
        return clone;
    }
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * Removes also the field to var name mappings
     * @see eu.iksproject.rick.core.query.FieldQueryImpl#removeAllSelectedFields()
     */
    @Override
    public void removeAllSelectedFields() {
        super.removeAllSelectedFields();
        field2VarMappings.clear();
        varNum = 0;
    }
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && obj instanceof SparqlFieldQuery &&
            ((SparqlFieldQuery)obj).field2VarMappings.equals(field2VarMappings) &&
            ((SparqlFieldQuery)obj).varNum == varNum;
    }
    /**
     * Getter for the SPARQL SELECT representation of this FieldQuery
     * @return the SPARQL SELECT query
     */
    public String toSparqlSelect(boolean includeFields){
        return SparqlQueryUtils.createSparqlSelectQuery(this,includeFields,endpointType);
    }
    /**
     * Getter for the SPARQL CONSTRUCT representation of this FieldQuery
     * @return the SPARQL CONSTRUCT query
     */
    public String toSparqlConstruct(){
        return SparqlQueryUtils.createSparqlConstructQuery(this,endpointType);
    }
    @Override
    public String toString() {
        return super.toString()+" field->variable mappings: "+field2VarMappings;
    }
}
