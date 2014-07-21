package ejava.examples.daoex.dao;

/**
 * This is the base exception for example DAOs. Note the design 
 * decision that needs to be made between a checked Exception and 
 * an unchecked RuntimeException. By using checked Exceptions you 
 * are requiring all interfacing code to deal with the exception --
 * even if it cannot correct it. By using unchecked RuntimeExceptions
 * the interfacing business code can be written cleaner by not 
 * being polluted with infrastructure exceptions.
 */
public class DAOException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public DAOException() {}
    public DAOException(String message) { super(message); }
    
    //be wary of the exceptions that carry along infrastructure 
    //exceptions. Care should be taken higher up in the architecture
    //to fully process these exceptions locally and not propogate
    //the lower level exceptions to RMI clients. Classpath issues
    //can occur at that point.
    
    public DAOException(String message, Throwable rootCause) {
        super(message, rootCause);
    }
    public DAOException(Throwable rootCause) {
        super(rootCause);
    }
}
