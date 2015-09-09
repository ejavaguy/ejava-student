package ejava.projects.esales.bl;

import java.util.List;

import ejava.projects.esales.bo.Account;

public interface AccountMgmt {
	List<Account> getAccounts(int index, int count) throws AccountMgmtException;
}
