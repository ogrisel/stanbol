package eu.iksproject.fise.engines.salsadev.core.xml.converter;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import eu.iksproject.fise.engines.salsadev.core.xml.pojo.Keyword;

/**
 * XStream converter for {@link eu.iksproject.fise.engines.salsadev.core.xml.pojo.Keyword} class.
 *
 * @author <a href="mailto:aleksey.oborin@salsadev.com">Aleksey Oborin</a>
 * @version %I%, %G%
 */
public class KeywordConverter implements Converter {
    /**
     * {@inheritDoc}
     */
    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        Keyword keyword = new Keyword();
        keyword.setScore(Double.valueOf(reader.getAttribute("score")));
        keyword.setTerm(reader.getValue());
        return keyword;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canConvert(Class type) {
        return type.equals(Keyword.class);
    }
}
