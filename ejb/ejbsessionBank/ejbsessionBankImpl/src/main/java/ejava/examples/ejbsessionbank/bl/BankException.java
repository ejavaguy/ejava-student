package ejava.examples.ejbsessionbank.bl;

public class BankException extends Exception {
    private static final long serialVersionUID = 1L;
    public BankException() {} 
    public BankException(Throwable ex) { super(ex); }
    public BankException(String msg) { super(msg); }
    public BankException(String msg, Throwable ex) { super(msg, ex); }
}
