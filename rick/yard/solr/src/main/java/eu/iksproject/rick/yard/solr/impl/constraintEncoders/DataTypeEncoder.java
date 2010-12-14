package eu.iksproject.rick.yard.solr.impl.constraintEncoders;

import java.util.Arrays;
import java.util.Collection;

import eu.iksproject.rick.yard.solr.model.FieldMapper;
import eu.iksproject.rick.yard.solr.model.IndexDataType;
import eu.iksproject.rick.yard.solr.model.IndexField;
import eu.iksproject.rick.yard.solr.model.IndexValue;
import eu.iksproject.rick.yard.solr.model.IndexValueFactory;
import eu.iksproject.rick.yard.solr.query.ConstraintTypePosition;
import eu.iksproject.rick.yard.solr.query.EncodedConstraintParts;
import eu.iksproject.rick.yard.solr.query.IndexConstraintTypeEncoder;
import eu.iksproject.rick.yard.solr.query.IndexConstraintTypeEnum;
import eu.iksproject.rick.yard.solr.query.ConstraintTypePosition.PositionType;
import eu.iksproject.rick.yard.solr.utils.SolrUtil;
/**
 * Encodes the DataType to the field name.
 * @author Rupert Westenthaler
 *
 */
public class DataTypeEncoder implements IndexConstraintTypeEncoder<Object> {

    private static final ConstraintTypePosition PREFIX = new ConstraintTypePosition(PositionType.prefix);
    private static final ConstraintTypePosition SUFFIX = new ConstraintTypePosition(PositionType.suffux);
    protected final FieldMapper fieldMapper;
    protected final IndexValueFactory indexValueFactory;
    public DataTypeEncoder(IndexValueFactory indexValueFactory, FieldMapper fieldMapper) {
        if(fieldMapper == null){
            throw new IllegalArgumentException("The FieldMapper MUST NOT be NULL!");
        }
        if(indexValueFactory == null){
            throw new IllegalArgumentException("The IndexValueFactory MUST NOT be NULL!");
        }
        this.fieldMapper = fieldMapper;
        this.indexValueFactory = indexValueFactory;
    }
    @Override
    public void encode(EncodedConstraintParts constraint,Object value) throws IllegalArgumentException {
        IndexDataType indexDataType;
        if(value == null){
            indexDataType = null;
        } else if(value instanceof IndexDataType){
            indexDataType = (IndexDataType)value;
        } else if (value instanceof IndexField) {
            indexDataType = ((IndexField)value).getDataType();
        } else if (value instanceof IndexValue) {
            indexDataType = ((IndexValue)value).getType();
        } else {
            indexDataType = indexValueFactory.createIndexValue(value).getType();
        }
        if(indexDataType != null) {
            String[] prefixSuffix = fieldMapper.encodeDataType(indexDataType);

            if(prefixSuffix[0] != null){
                constraint.addEncoded(PREFIX, SolrUtil.escapeSolrSpecialChars(prefixSuffix[0]));
            }
            if(prefixSuffix[1] != null){
                constraint.addEncoded(SUFFIX, SolrUtil.escapeSolrSpecialChars(prefixSuffix[1]));
            }
        } // else nothing todo!
    }

    @Override
    public boolean supportsDefault() {
        return true;
    }

    @Override
    public Collection<IndexConstraintTypeEnum> dependsOn() {
        return Arrays.asList(IndexConstraintTypeEnum.FIELD);
    }

    @Override
    public IndexConstraintTypeEnum encodes() {
        return IndexConstraintTypeEnum.DATATYPE;
    }
    @Override
    public Class<Object> acceptsValueType() {
        return Object.class;
    }


}
