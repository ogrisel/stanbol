package eu.iksproject.rick.core.query;

import eu.iksproject.rick.servicesapi.query.FieldQuery;
import eu.iksproject.rick.servicesapi.query.FieldQueryFactory;

/**
 * Simple {@link FieldQueryFactory} implementation that uses the singleton
 * pattern and returns for each call to {@link #createFieldQuery()} a new
 * instance of {@link FieldQueryImpl}.
 *
 * @author Rupert Westenthaler
 */
public class DefaultQueryFactory implements FieldQueryFactory {

    private static final DefaultQueryFactory instance = new DefaultQueryFactory();

    public static FieldQueryFactory getInstance() {
        return instance;
    }

    protected DefaultQueryFactory() {
        super();
    }

    @Override
    public FieldQuery createFieldQuery() {
        return new FieldQueryImpl();
    }

}
