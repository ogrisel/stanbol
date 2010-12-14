package eu.iksproject.rick.site.linkedData.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.clerezza.rdf.core.MGraph;
import org.apache.clerezza.rdf.core.TripleCollection;
import org.apache.clerezza.rdf.core.impl.SimpleMGraph;
import org.apache.clerezza.rdf.core.serializedform.Parser;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Reference;
import org.slf4j.LoggerFactory;

import eu.iksproject.rick.core.query.QueryResultListImpl;
import eu.iksproject.rick.core.site.AbstractEntitySearcher;
import eu.iksproject.rick.query.clerezza.RdfQueryResultList;
import eu.iksproject.rick.query.clerezza.SparqlFieldQuery;
import eu.iksproject.rick.query.clerezza.SparqlFieldQueryFactory;
import eu.iksproject.rick.query.clerezza.SparqlQueryUtils.EndpointTypeEnum;
import eu.iksproject.rick.servicesapi.model.Representation;
import eu.iksproject.rick.servicesapi.query.FieldQuery;
import eu.iksproject.rick.servicesapi.query.QueryResultList;
import eu.iksproject.rick.servicesapi.site.EntitySearcher;

@Component(
        name="eu.iksproject.rick.site.VirtuosoSearcher",
        factory="eu.iksproject.rick.site.VirtuosoSearcherFactory",
        policy=ConfigurationPolicy.REQUIRE, //the queryUri and the SPARQL Endpoint are required
        specVersion="1.1"
        )
public class VirtuosoSearcher extends AbstractEntitySearcher implements EntitySearcher{
    @Reference
    protected Parser parser;

    public VirtuosoSearcher() {
        super(LoggerFactory.getLogger(VirtuosoSearcher.class));
    }

    @Override
    public QueryResultList<Representation> find(FieldQuery parsedQuery) throws IOException {
        long start = System.currentTimeMillis();
        final SparqlFieldQuery query = SparqlFieldQueryFactory.getSparqlFieldQuery(parsedQuery);
        query.setEndpointType(EndpointTypeEnum.Virtuoso);
        String sparqlQuery = query.toSparqlConstruct();
        long initEnd = System.currentTimeMillis();
        log.info("  > InitTime: "+(initEnd-start));
        log.info("  > SPARQL query:\n"+sparqlQuery);
        InputStream in = SparqlEndpointUtils.sendSparqlRequest(getQueryUri(), sparqlQuery, SparqlSearcher.DEFAULT_RDF_CONTENT_TYPE);
        long queryEnd = System.currentTimeMillis();
        log.info("  > QueryTime: "+(queryEnd-initEnd));
        if(in != null){
            MGraph graph;
            TripleCollection rdfData = parser.parse(in, SparqlSearcher.DEFAULT_RDF_CONTENT_TYPE);
            if(rdfData instanceof MGraph){
                graph = (MGraph) rdfData;
            } else {
                graph = new SimpleMGraph(rdfData);
            }
            long parseEnd = System.currentTimeMillis();
            log.info("  > ParseTime: "+(parseEnd-queryEnd));
            return new RdfQueryResultList(query, graph);
        } else {
            return null;
        }
    }

    @Override
    public QueryResultList<String> findEntities(FieldQuery parsedQuery) throws IOException {
        final SparqlFieldQuery query = SparqlFieldQueryFactory.getSparqlFieldQuery(parsedQuery);
        query.setEndpointType(EndpointTypeEnum.Virtuoso);
        String sparqlQuery = query.toSparqlSelect(false);
        InputStream in = SparqlEndpointUtils.sendSparqlRequest(getQueryUri(), sparqlQuery, SparqlSearcher.DEFAULT_SPARQL_RESULT_CONTENT_TYPE);
        //Move to util class!
        final List<String> entities = SparqlSearcher.extractEntitiesFromJsonResult(in,query.getRootVariableName());
        return new QueryResultListImpl<String>(query, entities.iterator(),String.class);
    }

}
