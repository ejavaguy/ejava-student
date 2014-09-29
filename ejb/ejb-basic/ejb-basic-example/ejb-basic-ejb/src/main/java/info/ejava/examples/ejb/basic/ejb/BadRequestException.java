package info.ejava.examples.ejb.basic.ejb;

/**
 * This exception is thrown to report a bad name given.
 */
@SuppressWarnings("serial")
public class BadRequestException extends Exception {
    public BadRequestException(String message) {
        super(message);
    }
}
