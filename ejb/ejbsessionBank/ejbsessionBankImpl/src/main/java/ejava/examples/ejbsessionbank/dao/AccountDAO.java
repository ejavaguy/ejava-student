package ejava.examples.ejbsessionbank.dao;

import java.util.List;
import java.util.Map;

import ejava.examples.ejbsessionbank.bo.Account;
import ejava.examples.ejbsessionbank.bo.Ledger;

public interface AccountDAO {
    String GET_ACCOUNTS_QUERY = "getAccounts";
    
    Account getAccountById(long id) throws DAOException;
    Account getAccountByNum(String acctNum) throws DAOException;
    Account createAccount(Account account) throws DAOException;
    Account updateAccount(Account account) throws DAOException;
    Account removeAccount(Account account) throws DAOException;
    List<Account> findAccounts(String queryName, Map<String, Object> params,
            int index, int count) throws DAOException;
    long getLedgerCount() throws DAOException;
    double getLedgerAveBalance() throws DAOException;
    double getLedgerSum() throws DAOException;
    Ledger getLedger() throws DAOException;
}
