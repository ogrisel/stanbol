package eu.iksproject.fise.engines.salsadev.core.xml.pojo;

import java.io.Serializable;

/**
 * {@link Metadata} POJO class, contains information about metadata.
 *
 * @author <a href="mailto:aleksey.oborin@salsadev.com">Aleksey Oborin</a>
 * @version %I%, %G%
 */
public class Metadata implements Serializable {
    /**
     * Metadata name.
     */
    private String name;

    /**
     * Metadata value.
     */
    private String value;

    /**
     * Gets name of metadata.
     *
     * @return name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets new name of metadata.
     *
     * @param name is new name.
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Gets value of metadata.
     *
     * @return value.
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets new value of metadata.
     *
     * @param value is new value.
     */
    public void setValue(final String value) {
        this.value = value;
    }
}
