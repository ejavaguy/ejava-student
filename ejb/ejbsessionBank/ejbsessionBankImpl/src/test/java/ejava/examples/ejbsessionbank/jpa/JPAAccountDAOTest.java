package ejava.examples.ejbsessionbank.jpa;

import static org.junit.Assert.*;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;

import ejava.examples.ejbsessionbank.DemoBase;
import ejava.examples.ejbsessionbank.bo.Account;
import ejava.examples.ejbsessionbank.bo.Ledger;
import ejava.examples.ejbsessionbank.dao.AccountDAO;
import ejava.examples.ejbsessionbank.jpa.JPAAccountDAO;

/**
 * This class tests the implementation of the JPAAccountDAO.
 * @author jcstaff
 *
 */
public class JPAAccountDAOTest extends DemoBase {
    private static Logger log = LoggerFactory.getLogger(JPAAccountDAOTest.class);
    protected AccountDAO accountDAO;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        
        accountDAO = new JPAAccountDAO();
        ((JPAAccountDAO)accountDAO).setEntityManager(em);
    }

    @Test
    public void testCreateAccount() {
        log.info("*** testCreateAccount ***");
        
        Account account = new Account();
        account.setAccountNumber("1");
        
        accountDAO.createAccount(account);
        
        Account account2 = accountDAO.getAccountById(account.getId());
        assertEquals("unexpected account number", 
                account.getAccountNumber(),
                account2.getAccountNumber());
        
        Account account3 = accountDAO.getAccountByNum(account.getAccountNumber());
        assertEquals("unexpected account id", 
                account.getId(),
                account3.getId());
    }

    @Test
    public void testFindAccounts() {
        log.info("*** testFindAccounts ***");
        
        for (int i=0; i< 100; i++) {
            Account account = new Account();
            account.setAccountNumber("" + i);
            accountDAO.createAccount(account);
        }
        
        for (int i=0; i< 100; i+= 20) {
            Collection<Account> accounts = 
                accountDAO.findAccounts(AccountDAO.GET_ACCOUNTS_QUERY, 
                                        null, 
                                        i, 
                                        20);
            assertEquals("unexpected number of accounts returned", 
                    20, accounts.size());
        }        
    }


    @Test
    public void testRemoveAccount() {
        log.info("*** testRemoveAccount ***");
        
        Account account = new Account();
        account.setAccountNumber("1");
        
        accountDAO.createAccount(account);
        
        Account account2 = accountDAO.getAccountById(account.getId());
        assertEquals("unexpected number of accounts after insert",1,
            accountDAO.findAccounts(
                    AccountDAO.GET_ACCOUNTS_QUERY, null, 0, 100).size());
        
        accountDAO.removeAccount(account2);
        assertEquals("unexpected number of accounts after remove",0,
            accountDAO.findAccounts(
                    AccountDAO.GET_ACCOUNTS_QUERY, null, 0, 100).size());
    }

    @Test
    public void testUpdateAccount() {
        log.info("*** testUpdateAccount");
        
        Account account = new Account();
        account.setAccountNumber("1");
        account = accountDAO.createAccount(account);
        
        account.deposit(10.00);
        em.getTransaction().commit();
        em.clear();

        Account account2 = accountDAO.getAccountById(account.getId());
        assertEquals("unexpected amount", 10.00, account2.getBalance(),1);
        
        Account account3 = 
            new Account(account.getId(), account.getAccountNumber());
        account3.deposit(20.00);
        accountDAO.updateAccount(account3);
        em.getTransaction().begin();
        em.getTransaction().commit();
        
        Account account4 = accountDAO.getAccountById(account.getId());
        assertEquals("unexpected amount", 20.00, account4.getBalance(),1);
        
    }

    @Test
    public void testGetLedger() {
        log.info("*** testGetLedger ***");
     
        double sumTotal = 0;
        long countTotal = 0;
        for (int i=0; i< 100; i++) {
            Account account = new Account();
            account.setAccountNumber("" + i);
            account.deposit(i);
            accountDAO.createAccount(account);
            sumTotal += account.getBalance();
            countTotal += 1;
        }
        
        Ledger ledger = accountDAO.getLedger();
        double aveBalance = accountDAO.getLedgerAveBalance();
        long count = accountDAO.getLedgerCount();
        double sum = accountDAO.getLedgerSum();
        
        assertEquals("unexpected sum", sumTotal, sum,1);
        assertEquals("unexpected ledger sum", 
                sumTotal, ledger.getTotalAssets(),1);
        
        assertEquals("unexpected ave", 
                aveBalance, ledger.getAverageAssets(),1);
        assertEquals("unexpected count", count, 
                countTotal, ledger.getNumberOfAccounts());
    }
}
