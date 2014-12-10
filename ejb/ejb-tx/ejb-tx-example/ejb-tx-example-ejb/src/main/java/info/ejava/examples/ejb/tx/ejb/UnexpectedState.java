package info.ejava.examples.ejb.tx.ejb;

@SuppressWarnings("serial")
public class UnexpectedState extends Exception {
    public UnexpectedState(String stateName, int expected, int actual) {
        super(String.format("unexpected state for %s, expected=%d, actual=%d", stateName, expected, actual));
    }
}
