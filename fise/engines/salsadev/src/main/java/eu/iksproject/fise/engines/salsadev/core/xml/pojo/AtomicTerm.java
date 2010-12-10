package eu.iksproject.fise.engines.salsadev.core.xml.pojo;

import java.io.Serializable;

/**
 * {@link AtomicTerm} POJO class.
 *
 * @author <a href="mailto:aleksey.oborin@salsadev.com">Aleksey Oborin</a>
 * @version %I%, %G%
 */
public class AtomicTerm implements Serializable {
    /**
     * Operator.
     */
    private String operator;

    /**
     * Key name.
     */
    private String key;

    /**
     * Key value.
     */
    private String value;

    /**
     * Gets key.
     *
     * @return key
     */
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Gets operator.
     *
     * @return operator
     */
    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    /**
     * Gets value.
     *
     * @return value.
     */
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
