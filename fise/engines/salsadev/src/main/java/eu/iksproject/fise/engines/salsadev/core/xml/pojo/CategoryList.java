package eu.iksproject.fise.engines.salsadev.core.xml.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link CategoryList} POJO class, contains list of category's hits and it is
 * used as XML wrapper for this list.
 *
 * @author <a href="mailto:aleksey.oborin@salsadev.com">Aleksey Oborin</a>
 * @version %I%, %G%
 */
public class CategoryList implements Serializable {
    /**
     * List o categories in the current categories list.
     */
    private List<Category> categories;

    /**
     * Gets list of category's hits.
     *
     * @return list of category's hits.
     */
    public List<Category> getCategories() {
        if (categories == null) {
            categories = new ArrayList<Category>();
        }
        return categories;
    }

    /**
     * Sets new list of category's hits.
     *
     * @param categories is list of category's hits.
     */
    public void setCategories(final List<Category> categories) {
        this.categories = categories;
    }
}
