package ejava.projects.edmv.dao;

/**
 * This class provides a base exception to report DAO checked exceptions.
 * 
 */
public class DAOException extends Exception {
    private static final long serialVersionUID = 1L;
    public DAOException(String message) { super(message); }
    public DAOException(String message, Exception ex) { super(message, ex); }
}
