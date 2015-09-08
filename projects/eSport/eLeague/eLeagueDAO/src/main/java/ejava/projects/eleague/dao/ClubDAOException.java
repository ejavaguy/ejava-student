package ejava.projects.eleague.dao;

/**
 * This class defines an unchecked exception that can be thrown by the
 * ClubDAO. A more general exception could be defined for all DAOs. This
 * exception should report plumbing errors with the infrastructure -- not
 * items like invalid inputs.
 * @author jcstaff
 *
 */
public class ClubDAOException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	public ClubDAOException(String message) { 
        super(message); 
    }
	public ClubDAOException(String message, Throwable th) { 
		super(message, th); 
    }
}
