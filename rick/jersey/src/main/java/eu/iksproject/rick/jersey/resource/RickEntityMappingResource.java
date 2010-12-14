package eu.iksproject.rick.jersey.resource;

import java.util.Collection;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.clerezza.rdf.core.TripleCollection;
import org.apache.clerezza.rdf.core.access.TcManager;
import org.apache.clerezza.rdf.core.serializedform.Serializer;
import org.apache.clerezza.rdf.core.serializedform.SupportedFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.iksproject.rick.core.query.QueryResultListImpl;
import eu.iksproject.rick.jersey.utils.JerseyUtils;
import eu.iksproject.rick.servicesapi.Rick;
import eu.iksproject.rick.servicesapi.RickException;
import eu.iksproject.rick.servicesapi.model.EntityMapping;
import eu.iksproject.rick.servicesapi.query.QueryResultList;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static org.apache.clerezza.rdf.core.serializedform.SupportedFormat.*;
import static org.apache.clerezza.rdf.core.serializedform.SupportedFormat.N_TRIPLE;
import static org.apache.clerezza.rdf.core.serializedform.SupportedFormat.RDF_JSON;

/**
 * RESTful interface for the {@link EntityMapping}s defined by the  {@link Rick}.
 *
 * @author Rupert Westenthaler
 */
@Path("/mapping")
//@ImplicitProduces(MediaType.TEXT_HTML + ";qs=2")
public class RickEntityMappingResource extends NavigationMixin {

//    /**
//     * The default result fields for /find queries is the reference to the
//     * mapped symbol and the mapped entity
//     */
//    private static final Collection<? extends String> DEFAULT_FIND_SELECTED_FIELDS =
//        Arrays.asList(
//                RdfResourceEnum.mappedEntity.getUri(),
//                RdfResourceEnum.mappedSymbol.getUri());


    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Rick rick;

    protected TcManager tcManager;

    protected Serializer serializer;

    protected TripleCollection entityCache;

    // bind the job manager by looking it up from the servlet request context
    public RickEntityMappingResource(@Context ServletContext context) {
        super();
        rick = (Rick) context.getAttribute(Rick.class.getName());
        tcManager = (TcManager) context.getAttribute(TcManager.class.getName());
        serializer = (Serializer) context.getAttribute(Serializer.class.getName());
    }

    @GET
    @Path("/")
    @Produces({APPLICATION_JSON, RDF_XML, N3, TURTLE, X_TURTLE, RDF_JSON, N_TRIPLE})
    public Response getMapping(@QueryParam("id") String reference, @Context HttpHeaders headers) throws WebApplicationException {
        log.info("/symbol/ POST Request");
        log.info("  > id    : " + reference);
        log.info("  > accept: " + headers.getAcceptableMediaTypes());
        if (reference == null || reference.isEmpty()) {
            //TODO: how to parse an error message
            throw new WebApplicationException(BAD_REQUEST);
        }
        EntityMapping mapping;
        try {
            mapping = rick.getMappingById(reference);
        } catch (RickException e) {
            throw new WebApplicationException(e, INTERNAL_SERVER_ERROR);
        }
        if (mapping == null) {
            throw new WebApplicationException(404);
        } else {
            MediaType acceptedMediaType = JerseyUtils.getAcceptableMediaType(headers, APPLICATION_JSON_TYPE);
            return Response.ok(mapping, acceptedMediaType).build();
        }
    }

    @GET
    @Path("/entity")
    @Produces({APPLICATION_JSON, RDF_XML, N3, TURTLE, X_TURTLE, RDF_JSON, N_TRIPLE})
    public Response getEntityMapping(@QueryParam("id") String entity, @Context HttpHeaders headers) throws WebApplicationException {
        log.info("/symbol/ POST Request");
        log.info("  > entity: " + entity);
        log.info("  > accept: " + headers.getAcceptableMediaTypes());
        if (entity == null || entity.isEmpty()) {
            //TODO: how to parse an error message
            throw new WebApplicationException(BAD_REQUEST);
        }
        EntityMapping mapping;
        try {
            mapping = rick.getMappingByEntity(entity);
        } catch (RickException e) {
            throw new WebApplicationException(e, INTERNAL_SERVER_ERROR);
        }
        if (mapping == null) {
            throw new WebApplicationException(404);
        } else {
            MediaType acceptedMediaType = JerseyUtils.getAcceptableMediaType(headers, APPLICATION_JSON_TYPE);
            return Response.ok(mapping, acceptedMediaType).build();
        }
    }

    @GET
    @Path("/symbol")
    @Produces({APPLICATION_JSON, RDF_XML, N3, TURTLE, X_TURTLE, RDF_JSON, N_TRIPLE})
    public Response getSymbolMappings(@QueryParam("id") String symbol, @Context HttpHeaders headers) throws WebApplicationException {
        log.info("/symbol/ POST Request");
        log.info("  > symbol: " + symbol);
        log.info("  > accept: " + headers.getAcceptableMediaTypes());
        if (symbol == null || symbol.isEmpty()) {
            //TODO: how to parse an error message
            throw new WebApplicationException(BAD_REQUEST);
        }
        Collection<EntityMapping> mappings;
        try {
            mappings = rick.getMappingsBySymbol(symbol);
        } catch (RickException e) {
            throw new WebApplicationException(e, INTERNAL_SERVER_ERROR);
        }
        if (mappings == null || mappings.isEmpty()) {
            throw new WebApplicationException(404);
        } else {
            MediaType acceptedMediaType = JerseyUtils.getAcceptableMediaType(headers, APPLICATION_JSON_TYPE);
            //TODO: Implement Support for list of Signs, Representations and Strings
            //      For now use a pseudo QueryResultList
            QueryResultList<EntityMapping> mappingResultList = new QueryResultListImpl<EntityMapping>(null, mappings, EntityMapping.class);
            return Response.ok(mappingResultList, acceptedMediaType).build();
        }
    }
}
