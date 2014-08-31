package ejava.examples.txhotel.dao;

@SuppressWarnings("serial")
public class DAOException extends RuntimeException {
    public DAOException(String msg) { super(msg); }
    public DAOException(Throwable ex) { super(ex); }
    public DAOException(String msg, Throwable ex) { super(msg, ex); }
}
