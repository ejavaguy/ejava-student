package ejava.examples.ejbsessionbank.blimpl;

import java.util.ArrayList;
import java.util.List;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.examples.ejbsessionbank.bl.BankException;
import ejava.examples.ejbsessionbank.bl.Teller;
import ejava.examples.ejbsessionbank.bo.Account;
import ejava.examples.ejbsessionbank.bo.Ledger;
import ejava.examples.ejbsessionbank.bo.Owner;
import ejava.examples.ejbsessionbank.dao.AccountDAO;
import ejava.examples.ejbsessionbank.dao.OwnerDAO;

/**
 * This class implements the business logic of the Teller. Its logically
 * implemented in 2 halves; Account and Owner + Account. The Account-only
 * half is used to demonstrate some of the basics. The Owner+Account half 
 * is used to add a small amount of complexity to the data model to demo
 * some lazy load issues within the remote facade/EJB layer.
 * @author jcstaff
 *
 */
public class TellerImpl implements Teller {
    Log log = LogFactory.getLog(TellerImpl.class);
    public static final String OVERDRAWN_ACCOUNTS = "getOverdrawnAccounts";
    public static final String ALL_ACCOUNTS = "getAccounts";
    private AccountDAO acctDAO;
    private OwnerDAO ownerDAO;
    
    public void setAcctDAO(AccountDAO acctDAO) {
        this.acctDAO = acctDAO;
    }
    public void setOwnerDAO(OwnerDAO ownerDAO) {
        this.ownerDAO = ownerDAO;
    }
    
    public Account createAccount(String accountNum) throws BankException {
        log.debug("createAccount(num=" + accountNum + ")");
        
        Account account = new Account();
        account.setAccountNumber(accountNum);
        account = acctDAO.createAccount(account);
        
        log.debug("account created:" + account);
        return account;        
    }

    public Account getAccount(String accountNum) throws BankException { 
        log.debug("getAccount(num=" + accountNum + ")");
        
        Account account = acctDAO.getAccountByNum(accountNum);
        if (account==null) {
            throw new BankException("unable to locate account:" + accountNum);
        }
        log.debug("found account:" + account);
        return account;
    }

    public List<Account> getOverdrawnAccounts(int index, int count) throws BankException {
        log.debug("getOverdrawnAccounts(" +
                "index=" + index + ", count=" + count + ")");
        
        List<Account> accounts = acctDAO.findAccounts(
                OVERDRAWN_ACCOUNTS, null, index, count);
        
        log.debug("found " + accounts.size() + " accounts");
        return accounts;
    }

    public List<Account> getAccounts(int index, int count) throws BankException {
        log.debug("getAccounts(" +
                "index=" + index + ", count=" + count + ")");
        
        List<Account> accounts = acctDAO.findAccounts(
                ALL_ACCOUNTS, null, index, count);
        
        log.debug("found " + accounts.size() + " accounts");
        return accounts;
    }

    public Account closeAccount(String accountNum) throws BankException {
        log.debug("removeAccount(num" + accountNum + ")");

        Account account = acctDAO.getAccountByNum(accountNum);
        if (account == null) {
            log.debug("account num found:" + accountNum);
            throw new BankException("unable to locate account:" + accountNum);
        }
        else if (account.getBalance() != 0) {
            log.debug("account balance not 0; bal=" + account.getBalance());
            throw new BankException("unable to close account, " +
                    "invalid balance:" + account.getBalance());
        }
        else {
        	//unlink the account from the owner first
        	for (Owner owner : ownerDAO.getAccountOwners(account)) {
	        	List<Account> accounts = new ArrayList<Account>(owner.getAccounts());
	        	for (Account a : accounts) {
	        		if (a.getId() == account.getId()) {
	        			owner.getAccounts().remove(a);
	        		}
	        	}
        	}
        	//now we can remove account
            acctDAO.removeAccount(account);
        }
        
        log.debug("removed account:" + account);
        return account;
    }

    public void updateAccount(Account account) throws BankException {
        log.debug("updateAccount(update=" + account + ")");

        Account updated = acctDAO.updateAccount(account);
        
        log.debug("updated account:" + updated);
    }
    public Ledger getLedger() throws BankException {
        return acctDAO.getLedger();
    }
    
    public double getLedgerAveBalance() throws BankException {
        return acctDAO.getLedgerAveBalance();
    }
    public long getLedgerCount() throws BankException {
        return acctDAO.getLedgerCount();
    }
    public double getLedgerSum() throws BankException {
        return acctDAO.getLedgerSum();
    }
    
    
    public Owner addOwner(long ownerId, String accountNumber)
            throws BankException {
        Owner owner = ownerDAO.getOwnerById(ownerId);
        if (owner == null) {
            throw new BankException("unable to locate owner:" + ownerId);
        }
        Account account = acctDAO.getAccountByNum(accountNumber);
        if (account == null) {
            throw new BankException("unable to locate account:"+accountNumber);
        }
        owner.getAccounts().add(account);
        return owner;
    }    
    
    public Owner createOwner(String firstName, String lastName, String ssn)
            throws BankException {
        Owner owner = new Owner();
        owner.setFirstName(firstName);
        owner.setLastName(lastName);
        owner.setSsn(ssn);
        return ownerDAO.createOwner(owner);
    }
    
    public void removeOwner(long ownerId) {
        Owner owner = ownerDAO.getOwnerById(ownerId);
        if (owner != null) {
            ownerDAO.removeOwner(owner);
        }
    }
    
    public List<Owner> getOwners(int index, int count) throws BankException {
        return ownerDAO.findOwners(
                OwnerDAO.GET_OWNERS_QUERY, null, index, count);
    }
    
    public Owner openAccount(long ownerId, String accountNumber)
            throws BankException {
        Owner owner = ownerDAO.getOwnerById(ownerId);
        if (owner == null) {
            throw new BankException("owner not found, id=" + ownerId);
        }
        
        Account account = new Account();
        account.setAccountNumber(accountNumber);
        owner.getAccounts().add(account);
        
        return owner;
    }    
}
