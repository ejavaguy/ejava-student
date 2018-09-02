package ejava.projects.esales.blimpl;

import java.util.List;

import ejava.projects.esales.bl.AccountMgmt;
import ejava.projects.esales.bl.AccountMgmtException;
import ejava.projects.esales.bo.Account;
import ejava.projects.esales.dao.AccountDAO;

/**
 * This class provides a simple example of the account mgmt implementation.
 *
 */
public class AccountMgmtImpl implements AccountMgmt {
	private AccountDAO accountDAO;
	
	public void setAccountDAO(AccountDAO accountDAO) {
		this.accountDAO = accountDAO;;
	}

	public List<Account> getAccounts(int index, int count) 
	    throws AccountMgmtException {
		
		if (count < 0) {
			throw new AccountMgmtException("count must be >= 0");
		}
		return accountDAO.getAccounts(index, count);
	}
}
