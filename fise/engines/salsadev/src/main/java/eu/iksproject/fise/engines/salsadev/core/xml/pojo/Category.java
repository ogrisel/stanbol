package eu.iksproject.fise.engines.salsadev.core.xml.pojo;

import java.io.Serializable;

/**
 * {@link Category} POJO class, contains information about category and its rate.
 *
 * @author <a href="mailto:aleksey.oborin@salsadev.com">Aleksey Oborin</a>
 * @version %I%, %G%
 */
public class Category implements Serializable {
    /**
     * Score value.
     */
    private Double score;
    /**
     * Category name.
     */
    private String name;

    /**
     * Gets score.
     *
     * @return score.
     */
    public Double getScore() {
        return score;
    }

    /**
     * Sets new score.
     *
     * @param score is new score.
     */
    public void setScore(final Double score) {
        this.score = score;
    }

    /**
     * Gets name of category.
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets new name of category.
     *
     * @param name is new name.
     */
    public void setName(final String name) {
        this.name = name;
    }
}
