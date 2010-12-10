package eu.iksproject.fise.engines.salsadev.core.xml.pojo;

import java.io.Serializable;

/**
 * {@link SearchDescriptor} POJO class.
 *
 * @author <a href="mailto:aleksey.oborin@salsadev.com">Aleksey Oborin</a>
 * @version %I%, %G%
 */
public class SearchDescriptor implements Serializable {
    /**
     * Query string.
     */
    private String query;
    /**
     * Query type.
     */
    private String type = "search";
    /**
     * Search constrain.
     */
    private ConstrainTerm queryconstrain;
    /**
     * Number of results to return.
     */
	private int numresults = 10;
    /**
     * Page number,
     */
	private int page = 0;
    /**
     * Threshold value.
     */
	private double threshold = 0.3;

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		String q = query.replaceAll("\n", " ").trim();
		this.query = q;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public ConstrainTerm getQueryconstrain() {
		return queryconstrain;
	}

	public void setQueryconstrain(ConstrainTerm queryconstrain) {
		this.queryconstrain = queryconstrain;
	}

	public int getNumresults() {
		return numresults;
	}

	public void setNumresults(int numresults) {
		this.numresults = numresults;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}
}
