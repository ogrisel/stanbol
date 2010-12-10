package eu.iksproject.fise.engines.salsadev.core;

/**
 * {@link SalsadevApiException} Exception class to indicate SalsaDev API related errors.
 *
 * @author <a href="mailto:aleksey.oborin@salsadev.com">Aleksey Oborin</a>
 * @version %I%, %G%
 */
public class SalsadevApiException extends Exception {

    private static final long serialVersionUID = 1L;

    public SalsadevApiException() {
        super();
    }

    public SalsadevApiException(String message) {
        super(message);
    }

    public SalsadevApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
