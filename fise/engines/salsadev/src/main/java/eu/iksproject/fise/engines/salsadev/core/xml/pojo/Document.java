package eu.iksproject.fise.engines.salsadev.core.xml.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * {@link Document} POJO class, contains information about document hit.
 *
 * @author <a href="mailto:aleksey.oborin@salsadev.com">Aleksey Oborin</a>
 * @version %I%, %G%
 */
public class Document implements Serializable {
    /**
     * Score value.
     */
    private double score;

    /**
     * content snippet.
     */
    private String snippet;

    /**
     * Document UID.
     */
    private String uid;

    /**
     * List of document's metadata.
     */
    private List<Metadata> metadata;

    /**
     * Gets score.
     *
     * @return score.
     */
    public double getScore() {
        return score;
    }

    /**
     * Sets new score.
     *
     * @param score is new score.
     */
    public void setScore(final double score) {
        this.score = score;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public List<Metadata> getMetadata() {
        return metadata;
    }

    public void setMetadata(List<Metadata> metadata) {
        this.metadata = metadata;
    }
}
