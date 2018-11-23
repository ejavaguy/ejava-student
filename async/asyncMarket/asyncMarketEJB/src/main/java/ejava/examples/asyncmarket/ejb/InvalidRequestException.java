package ejava.examples.asyncmarket.ejb;

public class InvalidRequestException extends ClientErrorException {
    private static final long serialVersionUID = 7297262753557569854L;
    
    public InvalidRequestException(String format, Object...args) {
        super(String.format(format, args));
    }
    public InvalidRequestException(String msg) {
        super(msg);
    }
}
