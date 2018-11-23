package ejava.examples.asyncmarket.ejb;

public class ClientErrorException extends Exception {
    private static final long serialVersionUID = 39051392873144184L;

    public ClientErrorException(String msg) {
        super(msg);
    }
}
