package eu.iksproject.fise.servicesapi.helper;

import java.util.Collection;

import org.apache.clerezza.rdf.core.MGraph;
import org.apache.clerezza.rdf.core.NonLiteral;

import eu.iksproject.fise.servicesapi.helper.impl.SimpleRdfEntityFactory;

/**
 * A Factory that creates proxies over rdf nodes.
 *
 * @author Rupert Westenthaler
 */
public abstract class RdfEntityFactory {

    /**
     * Creates a new factory for the parsed {@link MGraph} instance.
     *
     * @param graph the graph used by the proxies created by this factory to
     * read/write there data
     * @return the created factory
     */
    public static RdfEntityFactory createInstance(MGraph graph){
        return new SimpleRdfEntityFactory(graph);
    }

    /**
     * Getter for a proxy for the parsed rdf node that implements all the parsed
     * Interfaces. The interface parsed as type must extend {@link RdfEntity}.
     * Additional interfaces must not extend this interface.
     * <p>
     * Interfaces parsed as parameter:
     * <ul>
     * <li> SHOULD have an {@link Rdf} annotation. If that is the case, than the
     * according rdf:type statements are checks/added when the proxy is created
     * <li> all methods of the parsed interfaces MUST HAVE {@link Rdf}
     * annotations. Calling methods with missing annotations causes an
     * {@link IllegalStateException} at runtime
     * <li> all methods of the parsed interface MUST HAVE a return type or a
     * single parameter (e.g. void setSomething(String value) or String
     * getSomething). Methods with a parameter do set the parsed data. Methods
     * with a return type do read the data.
     * </ul>
     *
     * Proxies returned by this Factory:
     * <ul>
     * <li> MUST NOT have an internal state. They need to represent a view over
     * the current data within the {@link MGraph} instance. Direct changes to
     * the graph need to be reflected in calls to proxies.
     * <li> Implementations need to support {@link Collection} as parameter.
     * Collections need to represent a live view over the triples within the
     * {@link MGraph}. However iterators may throw a
     * {@link ConcurrentModificationException} if the graph changes while using
     * the iterator.
     * </ul>
     *
     * @param <T> The interface implemented by the returned proxy
     * @param rdfNode the rdfNode represented by the proxy (created if not
     * present in the Graph)
     * @param type The interface for the proxy. Needs to extend {@link RdfEntity}
     * @param additionalInterfaces Additional interfaces the proxy needs to
     * implement.
     *
     * @return A proxy representing the parsed rdf node and implementing all the
     * parsed interfaces
     * @throws IllegalArgumentException if the node is <code>null</code> or the
     * parsed interfaces do not fulfil the requirements as stated.
     * @throws NullPointerException if the parameter type, additionalInterfaces
     * or any entry of additionalInterfaces is <code>null</code>.
     */
    public abstract <T extends RdfEntity> T getProxy(NonLiteral rdfNode,
            Class<T> type, Class<?>... additionalInterfaces) throws IllegalArgumentException;

}
