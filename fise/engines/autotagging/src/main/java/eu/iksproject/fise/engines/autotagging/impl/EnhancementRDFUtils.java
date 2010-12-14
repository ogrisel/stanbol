package eu.iksproject.fise.engines.autotagging.impl;

import java.util.Collection;

import org.apache.clerezza.rdf.core.LiteralFactory;
import org.apache.clerezza.rdf.core.MGraph;
import org.apache.clerezza.rdf.core.NonLiteral;
import org.apache.clerezza.rdf.core.UriRef;
import org.apache.clerezza.rdf.core.impl.TripleImpl;

import eu.iksproject.autotagging.TagInfo;
import eu.iksproject.fise.servicesapi.EnhancementEngine;
import eu.iksproject.fise.servicesapi.helper.EnhancementEngineHelper;

import static eu.iksproject.fise.servicesapi.rdf.Properties.*;

public class EnhancementRDFUtils {

    private EnhancementRDFUtils() {
    }

    /**
     * @param literalFactory the LiteralFactory to use
     * @param graph the MGraph to use
     * @param contentItemId the contentItemId the enhancement is extracted from
     * @param relatedEnhancements enhancements this textAnnotation is related to
     * @param tag the related entity
     */
    public static UriRef writeEntityAnnotation(EnhancementEngine engine, LiteralFactory literalFactory,
            MGraph graph, UriRef contentItemId, Collection<NonLiteral> relatedEnhancements, TagInfo tag) {
        UriRef entityAnnotation = EnhancementEngineHelper.createEntityEnhancement(
                graph, engine, contentItemId);
        // first relate this entity annotation to the text annotation(s)
        for (NonLiteral enhancement: relatedEnhancements){
            graph.add(new TripleImpl(entityAnnotation,
                        DC_RELATION, enhancement));
        }
        UriRef entityUri = new UriRef(tag.getId());
        // add the link to the referred entity
        graph.add(new TripleImpl(entityAnnotation,
                FISE_ENTITY_REFERENCE, entityUri));
        graph.add(new TripleImpl(entityAnnotation,
                FISE_ENTITY_LABEL,
                literalFactory.createTypedLiteral(tag.getLabel())));
        graph.add(new TripleImpl(entityAnnotation,
                FISE_CONFIDENCE,
                literalFactory.createTypedLiteral(tag.getConfidence())));
        for (String entityType : tag.getType()) {
            graph.add(new TripleImpl(entityAnnotation,
                    FISE_ENTITY_TYPE, new UriRef(entityType)));
        }
        return entityAnnotation;
    }

}
