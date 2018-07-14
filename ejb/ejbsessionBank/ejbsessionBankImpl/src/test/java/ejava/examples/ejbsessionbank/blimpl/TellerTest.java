package ejava.examples.ejbsessionbank.blimpl;


import java.util.Collection;

import static org.junit.Assert.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;

import ejava.examples.ejbsessionbank.DemoBase;
import ejava.examples.ejbsessionbank.bl.BankException;
import ejava.examples.ejbsessionbank.bl.Teller;
import ejava.examples.ejbsessionbank.bo.Account;
import ejava.examples.ejbsessionbank.bo.Ledger;
import ejava.examples.ejbsessionbank.bo.Owner;
import ejava.examples.ejbsessionbank.dao.AccountDAO;
import ejava.examples.ejbsessionbank.dao.OwnerDAO;
import ejava.examples.ejbsessionbank.jpa.JPAAccountDAO;
import ejava.examples.ejbsessionbank.jpa.JPAOwnerDAO;

public class TellerTest extends DemoBase {
    private Logger log = LoggerFactory.getLogger(TellerTest.class);
    Teller teller;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        AccountDAO acctDAO = new JPAAccountDAO();
        ((JPAAccountDAO)acctDAO).setEntityManager(em);
        
        OwnerDAO ownerDAO = new JPAOwnerDAO();
        ((JPAOwnerDAO)ownerDAO).setEntityManager(em);
        
        teller = new TellerImpl();
        ((TellerImpl)teller).setAcctDAO(acctDAO);      
        ((TellerImpl)teller).setOwnerDAO(ownerDAO);
    }
    
    @Test
    public void testCreateAccount() throws BankException {
        Account account = teller.createAccount("111");
        assertNotNull("no account returned", account);
        
        Account account2 = teller.getAccount(account.getAccountNumber());
        assertNotNull("no account found", account2);        
    }
    
    @Test
    public void testCloseAccount() throws BankException {
        Account account = teller.createAccount("111");
        em.getTransaction().commit();
        em.clear();
        
        em.getTransaction().begin();        
        account.deposit(10.0);
        teller.updateAccount(account);
        em.getTransaction().commit();
        em.clear();       
        
        try {
            teller.closeAccount(account.getAccountNumber());
            fail("closed an account with funds");
        }
        catch (BankException expected) {
            log.debug("received expected exception for closeAccount:"+expected);
        }
        
        account.withdraw(account.getBalance());
        teller.updateAccount(account);
        
        teller.closeAccount(account.getAccountNumber());
    }
    
    @Test
    public void testGetOverdrawnAccounts() throws BankException {
        log.info("*** testGetOverdrawnAccounts ***");
        
        for(int i=0; i<100; i++) {
            teller.createAccount("" + i);
        }
        assertEquals("unexpected number of accounts", 
                100, teller.getAccounts(0, 1000).size());
        assertEquals("unexpected number of overdrawn accounts", 
                0, teller.getOverdrawnAccounts(0, 1000).size());
        em.getTransaction().commit();
        
        int index=0;
        for(Collection<Account> accounts = teller.getAccounts(index, 20);
            accounts.size() > 0;) {            
            em.clear();
            for(Account a : accounts) {
                a.withdraw(5.00);
                teller.updateAccount(a);
            }
            em.getTransaction().begin();
            em.getTransaction().commit();
            index+=20;
            accounts = teller.getAccounts(index, 20);
        }
        assertEquals("unexpected number of overdrawn accounts", 
                100, teller.getOverdrawnAccounts(0, 1000).size());

        for(Account a : teller.getAccounts(0, 1000)) {
            em.clear();
            a.deposit(5.00);
            teller.updateAccount(a);
            teller.closeAccount(a.getAccountNumber());
            em.getTransaction().begin();
            em.getTransaction().commit();
        }
        assertEquals("unexpected number of overdrawn accounts", 
                0, teller.getOverdrawnAccounts(0, 1000).size());
    }
    
    @Test
    public void testGetLedgerCount() throws BankException {
        double total = 0;
        for(int i=0; i<100; i++) {
            Account account = teller.createAccount("" + i);
            Account account2 = 
                new Account(account.getId(),account.getAccountNumber());
            account2.deposit(i);
            total += i;
            teller.updateAccount(account2);
        }
        assertEquals("unexpected total", total,
                teller.getLedgerSum(),1);
        assertEquals("unexpected ave", total/100,
                teller.getLedgerAveBalance(),1);
        assertEquals("unexpected count", 100,
                teller.getLedgerCount());
        
        Ledger ledger = teller.getLedger();
        assertEquals("unexpected ledger total",total,
                ledger.getTotalAssets(),1);
        assertEquals("unexpected ledger average",total/100,
                ledger.getAverageAssets(),1);
        assertEquals("unexpected ledger count",100,
                ledger.getNumberOfAccounts());        
    }
    
    @Test
    public void testOwner() throws BankException {
        Owner owner = teller.createOwner("joe", "smith", "123");
        em.getTransaction().commit();
        assertNotNull("no owner returned", owner);
        assertEquals("unexpected number of accounts", 0, 
                owner.getAccounts().size());
        em.clear();
        
        em.getTransaction().begin();
        owner = teller.openAccount(owner.getId(), "111");        
        em.getTransaction().commit();
        
        assertEquals("unexpected number of accounts", 1, 
                owner.getAccounts().size());
        assertEquals("unexpected account number", "111", 
                owner.getAccounts().iterator().next().getAccountNumber());
        em.clear();
        
        em.getTransaction().begin();
        Owner owner2 = teller.createOwner("mary", "smith", "456");
        em.getTransaction().commit();
        em.getTransaction().begin();
        owner2 = teller.addOwner(owner2.getId(), "111");
        owner2 = teller.openAccount(owner2.getId(), "222");
        em.getTransaction().commit();
        assertEquals("unexpected number of accounts", 2, 
                owner2.getAccounts().size());
        assertEquals("unexpected account number", "111", 
                owner2.getAccounts().iterator().next().getAccountNumber());
        em.clear();
        
        em.getTransaction().begin();
        Owner owner3 = teller.createOwner("suzy", "smith", "789");
        em.getTransaction().commit();
        em.getTransaction().begin();
        teller.addOwner(owner3.getId(), "222");
        em.getTransaction().commit();
        assertEquals("unexpected number of accounts", 1, 
                owner3.getAccounts().size());
        em.clear();
        
        assertEquals("unexpected number of owners",
                3, teller.getOwners(0, 100).size());        
        assertEquals("unexpected number of accounts", 2, 
                teller.getAccounts(0, 100).size());
        
        em.getTransaction().begin();
        teller.removeOwner(owner.getId());
        em.getTransaction().commit();
        assertEquals("unexpected number of owners",
                2, teller.getOwners(0, 100).size());        
        assertEquals("unexpected number of accounts", 2, 
                teller.getAccounts(0, 100).size());

        em.getTransaction().begin();
        teller.removeOwner(owner2.getId());
        em.getTransaction().commit();
        assertEquals("unexpected number of owners",
                1, teller.getOwners(0, 100).size());        
        assertEquals("unexpected number of accounts", 1, 
                teller.getAccounts(0, 100).size());
        
        em.getTransaction().begin();
        teller.removeOwner(owner3.getId());
        em.getTransaction().commit();
        assertEquals("unexpected number of owners",
                0, teller.getOwners(0, 100).size());        
        assertEquals("unexpected number of accounts", 0, 
                teller.getAccounts(0, 100).size());
    }    
}
