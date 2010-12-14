package eu.iksproject.rick.model.clerezza.impl;

import org.apache.clerezza.rdf.core.InvalidLiteralTypeException;
import org.apache.clerezza.rdf.core.Literal;
import org.apache.clerezza.rdf.core.LiteralFactory;
import org.apache.clerezza.rdf.core.NoConvertorException;
import org.apache.clerezza.rdf.core.PlainLiteral;
import org.apache.clerezza.rdf.core.TypedLiteral;
import org.apache.clerezza.rdf.core.impl.SimpleLiteralFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.iksproject.rick.core.utils.AdaptingIterator.Adapter;
import eu.iksproject.rick.model.clerezza.RdfResourceUtils;
import eu.iksproject.rick.model.clerezza.RdfValueFactory;
import eu.iksproject.rick.servicesapi.model.Text;

/**
 * This Adapter supports:
 * <ul>
 * <li> String: Converts all Literal to there lexical form
 * <li> Text: Converts {@link PlainLiteral}s and {@link TypedLiteral}s with a
 * data type constrained in {@link RdfResourceUtils#STRING_DATATYPES} to Text instances
 * <li> Int, Long, UriRef ... : Converts {@link TypedLiteral}s to the according
 * Java Object by using the Clerezza {@link LiteralFactory} (see {@link SimpleLiteralFactory})
 * </ul>
 *
 * @author Rupert Westenthaler
 *
 * @param <T> All types of Literals
 * @param <A> See above documentation
 */
public class LiteralAdapter<T extends Literal,A> implements Adapter<T, A> {

    static Logger log = LoggerFactory.getLogger(LiteralAdapter.class);

    LiteralFactory lf = LiteralFactory.getInstance();
    RdfValueFactory valueFactory = RdfValueFactory.getInstance();

    @SuppressWarnings("unchecked")
    @Override
    public A adapt(T value, Class<A> type) {
        if(type.equals(String.class)){
            return (A) value.getLexicalForm();
        } else if(Text.class.isAssignableFrom(type)){
            if(value instanceof PlainLiteral ||
                    (value instanceof TypedLiteral &&
                    RdfResourceUtils.STRING_DATATYPES.contains(((TypedLiteral)value).getDataType()))){
                            return (A)valueFactory.createText(value);
            } else { //this Literal can not be converted to Text!
                if(value instanceof TypedLiteral){ //TODO: maybe remove this debugging for performance reasons
                    log.debug("TypedLiterals of type "+((TypedLiteral)value).getDataType()+" can not be converted to Text");
                } else {
                    log.warn("Literal of type"+value.getClass()+" are not supported by this Adapter");
                }
                return null;
            }
        } else if(TypedLiteral.class.isAssignableFrom(value.getClass())){
            try {
                return lf.createObject(type, (TypedLiteral)value);
            } catch (NoConvertorException e) {
                //This usually indicates a missing converter ... so log in warning
                log.warn("unable to convert "+value+" to "+type,e);
                return null;
            } catch (InvalidLiteralTypeException e) {
                //This usually indicated a wrong type of the literal so log in debug
                log.debug("unable to Literal "+value+" to the type"+type,e);
                return null;
            }
        } else {
            //indicates, that someone wants to convert non TypedLiterals to an
            //specific data type
            log.warn("Converting Literals without type information to types other " +
                    "String is not supported (requested type: "+type+")! -> return null");
            return null;
        }
    }
}
