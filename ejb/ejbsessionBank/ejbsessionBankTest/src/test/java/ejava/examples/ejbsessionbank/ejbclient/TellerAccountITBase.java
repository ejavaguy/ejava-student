package ejava.examples.ejbsessionbank.ejbclient;

import static org.junit.Assert.*;


import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import ejava.examples.ejbsessionbank.bl.BankException;
import ejava.examples.ejbsessionbank.bo.Account;
import ejava.examples.ejbsessionbank.bo.Ledger;

/**
 * This class contains the core tests for the teller remote. Derived classes
 * will show how to provide a remote stub to the server-side teller using
 * specific techniques. The base class provides common setup and teardown 
 * logic.
  */
public class TellerAccountITBase extends TellerRemoteITBase {
    private static final Log log = LogFactory.getLog(TellerAccountITBase.class);

    /**
     * Tests ability to create an account from the remote client.
     * @throws Exception
     */
    @Test
    public void testCreateAccount() throws Exception {
        log.info("*** testCreateAccount ***");
        Account account=null;
      
        	//try with what should be a unique number
        try {
            account = teller.createAccount("1234");
            log.debug("account created:" + account);
        }
        catch (Exception ex) {
            log.fatal("error creating account:" + ex, ex);
            fail("error creating account:" + ex);
        }        
        
        	//try with what we know is a duplicate number
        try {
            teller.createAccount(account.getAccountNumber());
            fail("created account with duplicate number");
        }
        catch (Exception expected) {
            log.info("got expected exception trying to create " +
                    "duplicate account:" + expected);
        }
    }
    
    /**
     * Tests ability to get an account by number.
     * @throws Exception
     */
    @Test
    public void testGetAccount() throws Exception {
        log.info("*** testGetAccount ***");
        try {
            Account account = teller.createAccount("1234");
            log.debug("account created:" + account);
            Account account2 = teller.getAccount(account.getAccountNumber());
            log.debug("got account:" + account);
            
            assertEquals("unexpected account num:"+account2.getAccountNumber(),
                    account.getAccountNumber(), account2.getAccountNumber());
            assertEquals("unexpected account bal:"+account2.getBalance(),
                    (int)(100*account.getBalance()), 
                    (int)(100*account2.getBalance()));
        }
        catch (Exception ex) {
            log.fatal("error getting account:" + ex, ex);
            fail("error getting account:" + ex);
        }        
    }    

    /**
     * Tests ability to update account.
     * @throws Exception
     */
    @Test
    public void testUpdateAccount() throws Exception {
        log.info("*** testUpdateAccount ***");
        Account account = null;
        
        log.debug("creating account, teller=" + teller);
        try {
            account = teller.createAccount("1234");
            log.debug("account created:" + account);
            
            account.deposit(5.00);
            assertEquals("unexpected balance:" + account.getBalance(),
                    5.00, 
                    account.getBalance(), .1);
            teller.updateAccount(account);
            log.debug("updated account:" + account);
            
            Account account2 = teller.getAccount(account.getAccountNumber());
            log.debug("retrieved updated account:" + account2);
                        
            assertEquals("unexpected account bal:"+account2.getBalance(),
                    account.getBalance(), 
                    account2.getBalance(), .1);
            
            account.withdraw(10.00);
            assertEquals("unexpected balance:" + account.getBalance(),
		            -5.00,
		            account.getBalance(), .1);
            teller.updateAccount(account);
            log.debug("updated account:" + account);
            
            account2 = teller.getAccount(account.getAccountNumber());
            log.debug("retrieved updated account:" + account2);
                        
            assertEquals("unexpected account bal:"+account2.getBalance(),
                    account.getBalance(), 
                    account2.getBalance(), .1);
        }
        catch (Exception ex) {
            log.fatal("error updating account:" + ex, ex);
            fail("error updating account:" + ex);
        }        
    }    
    
    /**
     * Tests ability to close an account.
     * @throws Exception
     */
    @Test
    public void testCloseAccount() throws Exception {
        log.info("*** testCloseAccount ***");

        try {
            Account account = teller.createAccount("1234");
            account.deposit(10.0);
            teller.updateAccount(account);
            try {
                log.debug("trying to close account with + balance:" + account);
                teller.closeAccount(account.getAccountNumber());
                fail("account was reported closed");
            }
            catch (BankException expected) {}            
            
            account.withdraw(20.0);
            teller.updateAccount(account);
            try {
                log.debug("trying to close account with - balance:" + account);
                teller.closeAccount(account.getAccountNumber());
                fail("account was reported closed");
            }
            catch (BankException expected) {}
            
            account.deposit(10.0);
            teller.updateAccount(account);
            log.debug("trying to close account with no balance:" + account);
            teller.closeAccount(account.getAccountNumber());
        }
        catch (BankException ex) {
            log.fatal("error getting overdrawn accounts:" + ex, ex);
            fail("error getting overdrawn accounts:" + ex);
        }
    }

    /**
     * Tests query method of teller.
     * @throws Exception
     */
    @Test
    public void testFindOverdrawnAccounts() throws Exception {
        log.info("*** testFindOverdrawnAccounts ***");
        try {
            int num = 0;
            Account account1 = teller.createAccount("" + ++num);
            @SuppressWarnings("unused")
            Account account2 = teller.createAccount("" + ++num);
            Account account3 = teller.createAccount("" + ++num);
            log.debug("created 3 accounts");

            account1.deposit(10.0);
            teller.updateAccount(account1);                
            account3.withdraw(10.0);
            teller.updateAccount(account3);                
            log.debug("updated 2 accounts");
            
            List<Account> accounts=teller.getOverdrawnAccounts(0, 100);
            log.debug("overdrawn accounts:" + accounts);
            assertEquals("unexpected number of accounts:"+accounts, 
                    1, accounts.size());
        }
        catch (BankException ex) {
            log.fatal("error getting overdrawn accounts:" + ex, ex);
            fail("error getting overdrawn accounts:" + ex);
        }
    }

    /**
     * Tests ability to find all accounts using paging controls.
     * @throws Exception
     */
    @Test
    public void testFindAllAccounts() throws Exception {
        log.info("*** testFindAllAccounts ***");

        int TOTAL = 20;
        try {
            for(int i=0; i<TOTAL; i++) {
                Account account = teller.createAccount("" + i);
                account.deposit(i);
                teller.updateAccount(account);                
            }
            
            int index=0;
            for(List<Account> accounts=teller.getAccounts(index, TOTAL/5);
                accounts.size() > 0;
                accounts = teller.getAccounts(index, TOTAL/5)) {
                log.debug("got " + accounts.size() + " accounts");
                for(Account a: accounts) {
                    assertEquals("unexpected balance",
	                    index++, 
	                    a.getBalance(),.1);
                }
            }            
            assertEquals("unexpected number of accounts:"+index, TOTAL, index);
        }
        catch (BankException ex) {
            log.fatal("error getting accounts:" + ex, ex);
            fail("error getting accounts:" + ex);
        }
    }
    
    /**
     * Tests ability to get a ledger summary for all accounts using the 
     * brute force technique on the server-side.
     * @throws Exception
     */
    @Test
    public void testGetLedger() throws Exception {
        log.info("*** testGetLedger ***");
        int TOTAL = 20;
        try {
            for(int i=0; i<TOTAL; i++) {
                Account account = teller.createAccount("" + i);
                account.deposit(i);
                teller.updateAccount(account);                
            }

            Ledger ledger = teller.getLedger();
            assertNotNull("ledger is null", ledger);
            log.debug("got ledger:" + ledger);
            
            assertEquals("unexpected number of accounts:"+
                    ledger.getNumberOfAccounts(), 
                    TOTAL, ledger.getNumberOfAccounts());
        }
        catch (BankException ex) {
            log.fatal("error getting ledger:" + ex, ex);
            fail("error getting ledger:" + ex);
        }
    }

    /**
     * Tests ability to get a ledger summary for all accounts using a 
     * database query to populate the DTO.
     * @throws Exception
     */
    @Test
    public void testGetLedger2() throws Exception {
        log.info("*** testGetLedger2 ***");
        int TOTAL = 20;

        try {
            for(int i=0; i<TOTAL; i++) {
                Account account = teller.createAccount("" + i);
                account.deposit(i);
                teller.updateAccount(account);                
            }

            Ledger ledger = teller.getLedger2();
            assertNotNull("ledger is null", ledger);
            log.debug("got ledger:" + ledger);
            
            assertEquals("unexpected number of accounts:"+
                    ledger.getNumberOfAccounts(), 
                    TOTAL, ledger.getNumberOfAccounts());
        }
        catch (BankException ex) {
            log.fatal("error getting ledger2:" + ex, ex);
            fail("error getting ledger2:" + ex);
        }
    }
    
    @Test
    public void testStats() throws BankException {
    	log.info("*** testStats ***");
    	stats.reset();
    	assertEquals("unexpected delta", 0, stats.getDelta());
    	assertEquals("unexpected total", 0, stats.getTotal());
    	
    	//create some accounts
    	int createCount=10;
    	for (int i=0; i<createCount; i++) {
    		teller.createAccount(""+i);
    	}
    	
    	log.debug(String.format("delta=%d, total=%d", stats.getDelta(), stats.getTotal()));
    	assertEquals("unexpected delta", createCount, stats.getDelta());
    	assertEquals("unexpected total", createCount, stats.getTotal());

    	//close some accounts
    	int closeCount=5;
    	for (int i=0; i<closeCount; i++) {
    		teller.closeAccount(""+i);
    	}
    	
    	log.debug(String.format("delta=%d, total=%d", stats.getDelta(), stats.getTotal()));
    	assertEquals("unexpected delta", createCount-closeCount, stats.getDelta());
    	assertEquals("unexpected total", createCount+closeCount, stats.getTotal());
    }
}
