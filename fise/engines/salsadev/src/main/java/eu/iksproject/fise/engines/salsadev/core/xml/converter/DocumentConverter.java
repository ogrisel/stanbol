package eu.iksproject.fise.engines.salsadev.core.xml.converter;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import eu.iksproject.fise.engines.salsadev.core.xml.pojo.Document;
import eu.iksproject.fise.engines.salsadev.core.xml.pojo.Metadata;

import java.util.ArrayList;
import java.util.List;

/**
 * XStream converter for {@link eu.iksproject.fise.engines.salsadev.core.xml.pojo.Document} class
 *
 * @author <a href="mailto:aleksey.oborin@salsadev.com">Aleksey Oborin</a>
 * @version %I%, %G%
 */
public class DocumentConverter implements Converter {
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
        Document dto = new Document();
        List<Metadata> metadatas = new ArrayList<Metadata>();
        dto.setMetadata(metadatas);
        dto.setUid(reader.getAttribute("uid"));

        while (reader.hasMoreChildren()) {
            reader.moveDown();
            if (reader.getNodeName().equals("metadata")) {
                Metadata metadata = new Metadata();
                metadata.setName(reader.getAttribute("name"));
                metadata.setValue(reader.getValue());
                metadatas.add(metadata);
            }
            else if (reader.getNodeName().equals("snippet")) {
                dto.setSnippet(reader.getValue());
            }
            else if (reader.getNodeName().equals("score")) {
                dto.setScore(Double.valueOf(reader.getValue()));
            }
            reader.moveUp();
        }

        return dto;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canConvert(Class type) {
        return type.equals(Document.class);
    }
}
