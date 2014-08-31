package ejava.examples.webtier.dao;

public class StudentDAOException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public StudentDAOException() {}
    public StudentDAOException(String message) { super(message); }
    public StudentDAOException(String message, Throwable th) { 
        super(message, th); 
    }
    public StudentDAOException(Throwable th) { 
        super(th); 
    }
}
