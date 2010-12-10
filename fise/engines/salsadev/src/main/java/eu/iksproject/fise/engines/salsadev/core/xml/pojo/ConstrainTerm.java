package eu.iksproject.fise.engines.salsadev.core.xml.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link ConstrainTerm} POJO class.
 *
 * @author <a href="mailto:aleksey.oborin@salsadev.com">Aleksey Oborin</a>
 * @version %I%, %G%
 */
public class ConstrainTerm implements Serializable {
    /**
     * constrain operator, e.g AND.
     */
    private String operator;

    /**
     * List of atomic constrains.
     */
    private List<AtomicTerm> atomics;

    /**
     * List of sub constrains.
     */
    private List<ConstrainTerm> constrains;

    public String getOperator() {
        return operator;
    }

    public void setOperator(final String operator) {
        this.operator = operator;
    }

    public List<AtomicTerm> getAtomics() {
        if (atomics == null) {
            atomics = new ArrayList<AtomicTerm>();
        }
        return atomics;
    }

    public void setAtomics(final List<AtomicTerm> atomics) {
        this.atomics = atomics;
    }

    public List<ConstrainTerm> getConstrains() {
        if (constrains == null) {
            constrains = new ArrayList<ConstrainTerm>();
        }
        return constrains;
    }

    public void setConstrains(final List<ConstrainTerm> constrains) {
        this.constrains = constrains;
    }

}
