package ejava.examples.asyncmarket.ejb;

public class InternalErrorException extends RuntimeException {
    private static final long serialVersionUID = 5244973890768213163L;
    
    public InternalErrorException(String format, Object...args) {
        super(String.format(format, args));
    }
    public InternalErrorException(String msg) {
        super(msg);
    }
    public InternalErrorException(Throwable ex, String msg) {
        super(msg, ex);
    }
    public InternalErrorException(Throwable ex, String format, Object...args) {
        super(String.format(format, args), ex);
    }
}
