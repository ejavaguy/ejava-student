package info.ejava.examples.ejb.cdisales.bl;

@SuppressWarnings("serial")
public class InvalidAccount extends Exception {
    public InvalidAccount(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidAccount(String message) {
        super(message);
    }
}
