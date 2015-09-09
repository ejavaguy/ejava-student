package ejava.projects.esales.dao;

import java.util.List;

import ejava.projects.esales.bo.Account;

public interface AccountDAO {
	void createAccount(Account account) 
		throws AccountDAOException;
	List<Account> getAccounts(int index, int count)
	    throws AccountDAOException;
}
