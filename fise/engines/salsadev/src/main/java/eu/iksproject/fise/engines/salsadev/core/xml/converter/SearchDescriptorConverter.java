package eu.iksproject.fise.engines.salsadev.core.xml.converter;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import eu.iksproject.fise.engines.salsadev.core.xml.pojo.AtomicTerm;
import eu.iksproject.fise.engines.salsadev.core.xml.pojo.ConstrainTerm;
import eu.iksproject.fise.engines.salsadev.core.xml.pojo.SearchDescriptor;

/**
 * XStream converter for
 * {@link eu.iksproject.fise.engines.salsadev.core.xml.pojo.SearchDescriptor}
 * class.
 *
 * @author <a href="mailto:aleksey.oborin@salsadev.com">Aleksey Oborin</a>
 * @version %I%, %G%
 */
public class SearchDescriptorConverter implements Converter {
    /**
     * {@inheritDoc}
     */
    @Override
    public void marshal(Object source, HierarchicalStreamWriter writer,
            MarshallingContext context) {
        SearchDescriptor searchDescriptor = (SearchDescriptor) source;
        writer.addAttribute("type", searchDescriptor.getType());
        writer.addAttribute("threshold",
                String.valueOf(searchDescriptor.getThreshold()));
        writer.addAttribute("page", String.valueOf(searchDescriptor.getPage()));
        writer.addAttribute("numresults",
                String.valueOf(searchDescriptor.getNumresults()));

        writer.startNode("query");
        writer.setValue(searchDescriptor.getQuery());
        writer.endNode();

        writeConstrains(searchDescriptor, writer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object unmarshal(HierarchicalStreamReader reader,
            UnmarshallingContext context) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canConvert(Class type) {
        return type.equals(SearchDescriptor.class);
    }

    /**
     * Writes constrains if any.
     *
     * @param searchDescriptor current search descriptor.
     * @param writer stream writer.
     */
    private void writeConstrains(SearchDescriptor searchDescriptor,
            HierarchicalStreamWriter writer) {
        if (searchDescriptor.getQueryconstrain() != null) {
            writeConstrain(searchDescriptor.getQueryconstrain(), writer);
        }
    }

    /**
     * Writes constrain.
     *
     * @param constrainTerm current constrain.
     * @param writer stream writer.
     */
    private void writeConstrain(ConstrainTerm constrainTerm,
            HierarchicalStreamWriter writer) {
        writer.startNode("queryconstrain");
        writer.addAttribute("operator", constrainTerm.getOperator());

        if (constrainTerm.getConstrains() != null) {
            for (ConstrainTerm childConstrain : constrainTerm.getConstrains()) {
                writeConstrain(childConstrain, writer);
            }
        }

        if (constrainTerm.getAtomics() != null) {
            for (AtomicTerm atomicTerm : constrainTerm.getAtomics()) {
                writer.startNode("atomic");
                writer.addAttribute("operator", atomicTerm.getOperator());
                writer.addAttribute("key", atomicTerm.getKey());
                writer.setValue(atomicTerm.getValue());
                writer.endNode();
            }
        }

        writer.endNode();
    }
}
