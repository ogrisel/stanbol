package eu.iksproject.fise.engines.salsadev.core.xml.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link DocumentList} POJO class, contains list of document's hits and it is
 * used as XML wrapper for this list.
 *
 * @author <a href="mailto:aleksey.oborin@salsadev.com">Aleksey Oborin</a>
 * @version %I%, %G%
 */
public class DocumentList implements Serializable {
    /**
     * List of document hits in this document hits list.
     */
    private List<Document> documents;

    /**
     * Gets list of document hits.
     *
     * @return list of document hits.
     */
    public List<Document> getDocuments() {
        if (documents == null) {
            documents = new ArrayList<Document>();
        }
        return documents;
    }

    /**
     * Gets new list of document hits.
     *
     * @param documents is new list of document's hits.
     */
    public void setDocuments(final List<Document> documents) {
        this.documents = documents;
    }
}
