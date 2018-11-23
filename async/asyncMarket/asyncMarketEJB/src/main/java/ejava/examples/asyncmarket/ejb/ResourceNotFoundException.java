package ejava.examples.asyncmarket.ejb;

public class ResourceNotFoundException extends ClientErrorException {
    private static final long serialVersionUID = -3148800979343416687L;
    
    public ResourceNotFoundException(String format, Object...args) {
        super(String.format(format, args));
    }
    public ResourceNotFoundException(String msg) {
        super(msg);
    }
}
