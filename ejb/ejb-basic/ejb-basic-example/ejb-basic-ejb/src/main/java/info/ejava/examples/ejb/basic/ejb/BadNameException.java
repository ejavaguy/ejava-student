package info.ejava.examples.ejb.basic.ejb;

/**
 * This exception is thrown to report a bad name given.
 */
@SuppressWarnings("serial")
public class BadNameException extends Exception {
	public BadNameException(String message) {
		super(message);
	}
}
