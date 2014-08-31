package ejava.examples.ejbsessionbank.bo;

import javax.persistence.Query;

import static org.junit.Assert.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.examples.ejbsessionbank.DemoBase;

public class AccountTest extends DemoBase {
    Log log = LogFactory.getLog(AccountTest.class);
    
    public void testCreateAccount() {
        log.info("*** testCreateAccount ***");
        
        Account account = new Account();
        account.setAccountNumber("12345");
        account.deposit(5.0);
        
        em.persist(account);
        assertTrue("account not managed", em.contains(account));
        assertTrue("unexpected id:" + account.getId(), account.getId() != 0);        
    }
    
    public void testCreateOwner() {
        log.info("*** testCreateOwner ***");
        
        Owner owner1 = new Owner();
        owner1.setFirstName("joe");
        owner1.setLastName("smith");
        owner1.setSsn("111");
        
        Owner owner2 = new Owner();
        owner2.setFirstName("mary");
        owner2.setLastName("smith");
        owner2.setSsn("222");
        
        Account account1 = new Account();
        account1.setAccountNumber("x1");
        account1.deposit(10.0);
        
        owner1.getAccounts().add(account1);
        owner2.getAccounts().add(account1);
        em.persist(owner1);
        em.persist(owner2);
        
        em.flush();
        
        Owner owner3 = new Owner();
        owner3.setFirstName("jane");
        owner3.setLastName("smith");
        owner3.setSsn("333");
        
        Account account2 = new Account();
        account2.setAccountNumber("x2");
        
        owner3.getAccounts().add(account2);
        owner2.getAccounts().add(account2);
        em.persist(owner3);
        
        em.flush();
        
        owner1 = em.find(Owner.class,owner1.getId());
        assertEquals("unexpected number of owner1 accounts", 1, 
                owner1.getAccounts().size());

        owner2 = em.find(Owner.class,owner2.getId());
        assertEquals("unexpected number of owner2 accounts", 2, 
                owner2.getAccounts().size());
        
        owner3 = em.find(Owner.class,owner3.getId());
        assertEquals("unexpected number of owner3 accounts", 1, 
                owner3.getAccounts().size());
        
        em.getTransaction().commit();

    }
    
    public void testLedger() throws Exception {
        log.info("*** testLedger ***"); 
        
        for(int i=0; i<5; i++) {
            Account account = new Account();
            account.setAccountNumber(""+i);
            account.deposit(100*i);
            em.persist(account);        
        }
        em.flush();
        
        Query query = em.createQuery(
                "select new ejava.examples.ejbsessionbank.bo.Ledger(" +
                "count(a), sum(a.balance), avg(a.balance)) " +
                "from Account a");
        Object result = query.getSingleResult();
        if (result instanceof Object[]) {
            for(Object o: ((Object[])result)) {
                log.info("result=" + o);
            }
        }
        else {
            log.info("result=" + result);
        }
        
        Ledger ledger = 
            (Ledger)em.createNamedQuery("getLedger").getSingleResult();
        log.info("got ledger from named query:" + ledger);
        assertEquals(5, ledger.getNumberOfAccounts());
    }

}
