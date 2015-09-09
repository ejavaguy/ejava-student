package ejava.projects.esales.dao;

public class AccountDAOException extends Error {
	private static final long serialVersionUID = 1L;

	public AccountDAOException(String message, Throwable th) { 
		super(message, th); 
    }
}
