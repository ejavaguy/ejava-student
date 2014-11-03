package info.ejava.examples.ejb.cdisales.ejb;

@SuppressWarnings("serial")
public class InvalidProduct extends Exception {
    public InvalidProduct(String message) {
        super(message);
    }

}
