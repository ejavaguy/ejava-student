package ejava.examples.ejbsessionbank.bl;

import java.util.List;

import ejava.examples.ejbsessionbank.bo.Account;
import ejava.examples.ejbsessionbank.bo.Ledger;
import ejava.examples.ejbsessionbank.bo.Owner;

public interface Teller {
    Account createAccount(String accNum) throws BankException;    
    Account getAccount(String acctNum) throws BankException;    
    Account closeAccount(String acctNum) throws BankException;    
    void updateAccount(Account account) throws BankException;
    List<Account> getOverdrawnAccounts(int index, int count) 
        throws BankException;
    List<Account> getAccounts(int index, int count) 
        throws BankException;
    long getLedgerCount() throws BankException;
    double getLedgerAveBalance() throws BankException;
    double getLedgerSum() throws BankException;
    Ledger getLedger() throws BankException;
    
    Owner createOwner(String firstName, String lastName, String ssn)
        throws BankException;
    Owner openAccount(long ownerId, String accountNumber)
        throws BankException;
    Owner addOwner(long ownerId, String accountNumber)
        throws BankException;
    void removeOwner(long ownerId)
        throws BankException;
    List<Owner> getOwners(int index, int count)
        throws BankException;    
}
