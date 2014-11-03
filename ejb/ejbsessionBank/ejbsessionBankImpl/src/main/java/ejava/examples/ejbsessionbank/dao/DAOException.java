package ejava.examples.ejbsessionbank.dao;

public class DAOException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public DAOException() {}
    public DAOException(String msg) { super(msg); }
    public DAOException(Throwable ex) { super(ex); }
    public DAOException(String msg, Throwable ex) { super(msg, ex); }    
}
