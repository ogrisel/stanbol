package eu.iksproject.fise.engines.salsadev.core.xml.pojo;

import java.io.Serializable;
import java.util.Set;

/**
 * {@link KeywordList} POJO class.
 *
 * @author <a href="mailto:aleksey.oborin@salsadev.com">Aleksey Oborin</a>
 * @version %I%, %G%
 */
public class KeywordList implements Serializable {
    /**
     * Set of keywords in the current KeywordList.
     */
    private Set<Keyword> keywords;

    public Set<Keyword> getKeywords() {
        return keywords;
    }

    public void setKeywords(final Set<Keyword> keywords) {
        this.keywords = keywords;
    }
}
