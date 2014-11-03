package ejava.examples.webtier.bl;

public class RegistrarException extends Exception {
    private static final long serialVersionUID = 1L;
    public RegistrarException() {}
    public RegistrarException(String msg) { super(msg); }
    public RegistrarException(String msg, Throwable th) { super(msg, th); }
    public RegistrarException(Throwable th) { super(th); }
}
