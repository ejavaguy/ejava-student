package ejava.examples.orm.inheritance.annotated;

/**
 * This class is thrown by Account methods to report a business exception.
 *
 * @author jcstaff
 */
public class AccountException extends Exception {
    private static final long serialVersionUID = 1L;
    public AccountException() {}
    public AccountException(String message) { super(message); }
}
