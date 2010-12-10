package eu.iksproject.fise.engines.salsadev.core.xml.pojo;

import java.io.Serializable;

/**
 * {@link Keyword} POJO class.
 *
 * @author <a href="mailto:aleksey.oborin@salsadev.com">Aleksey Oborin</a>
 * @version %I%, %G%
 */
public class Keyword implements Serializable {
    /**
     * Stem of the keyword.
     */
    private String stem;
    /**
     * Term of the keyword. 
     */
    private String term;
    /**
     * Score value.
     */
    private double score;

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getStem() {
        return stem;
    }

    public void setStem(String stem) {
        this.stem = stem;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }
}
