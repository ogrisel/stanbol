package eu.iksproject.fise.engines.metaxa.core.html;

/**
 * <code>InitializationException</code> is thrown when an initialization step
 * fails.
 *
 * @author Joerg Steffen, DFKI
 * @version $Id$
 */
public class InitializationException extends Exception {

    /**
     * This creates a new instance of <code>InitializationException</code> with
     * null as its detail message. The cause is not initialized.
     */
    public InitializationException() {
        super();
    }

    /**
     * This creates a new instance of <code>InitializationException</code> with
     * the given detail message. The cause is not initialized.
     *
     * @param message
     *            a <code>String</code> with the detail message
     */
    public InitializationException(String message) {
        super(message);
    }

    /**
     * This creates a new instance of <code>InitializationException</code> with
     * the specified cause and a detail message of (cause==null ? null :
     * cause.toString()) (which typically contains the class and detail message
     * of cause).
     *
     * @param cause
     *            a <code>Throwable</code> with the cause of the exception
     *            (which is saved for later retrieval by the {@link #getCause()}
     *            method). (A <tt>null</tt> value is permitted, and indicates
     *            that the cause is nonexistent or unknown.)
     */
    public InitializationException(Throwable cause) {

        super(cause);
    }

    /**
     * This creates a new instance of <code>InitializationException</code> with
     * the given detail message and the given cause.
     *
     * @param message
     *            a <code>String</code> with the detail message
     * @param cause
     *            a <code>Throwable</code> with the cause of the exception
     *            (which is saved for later retrieval by the {@link #getCause()}
     *            method). (A <tt>null</tt> value is permitted, and indicates
     *            that the cause is nonexistent or unknown.)
     */
    public InitializationException(String message, Throwable cause) {

        super(message, cause);
    }

}
