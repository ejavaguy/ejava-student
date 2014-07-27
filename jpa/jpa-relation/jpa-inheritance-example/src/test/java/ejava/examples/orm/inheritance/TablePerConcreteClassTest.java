package ejava.examples.orm.inheritance;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ejava.examples.orm.inheritance.annotated.Account;
import ejava.examples.orm.inheritance.annotated.CheckingAccount;
import ejava.examples.orm.inheritance.annotated.InterestAccount;

/**
 * This class provides a demonstration of a class hierachy that uses separate
 * tables per concrete class mapping strategy.
 */
public class TablePerConcreteClassTest extends DemoBase {

	@Before
    public void setUp() throws Exception {
        em.createQuery("delete from CheckingAccount c").executeUpdate();
        em.createQuery("delete from InterestAccount i").executeUpdate();
        em.flush();
        em.getTransaction().commit();
        em.getTransaction().begin();
    }

    @SuppressWarnings("unchecked")
	@Test
    public void testTablePerConcreteClassCreate() {
        log.info("testTablePerConcreteClassCreate");
        
        ejava.examples.orm.inheritance.annotated.CheckingAccount checking = 
            new CheckingAccount();
        checking.setFee(0.50);
        em.persist(checking);
        
        ejava.examples.orm.inheritance.annotated.InterestAccount savings = 
            new InterestAccount();
        savings.setRate(0.25);
        em.persist(savings);
        
        em.flush();
        em.clear();
        assertFalse("checking still managed", em.contains(checking));
        assertFalse("savings still managed", em.contains(savings));
        
        List<Account> accounts =em.createQuery("select a from Account a").getResultList(); 
        assertTrue("unexpected number of accounts:" + accounts.size(),
                accounts.size() == 2);
        for(Account a: accounts) {
            log.info("account found:" + a);
        }        
        
        //query specific tables for columns
        int rows = em.createNativeQuery(
                "select ID, BALANCE, FEE from ORMINH_CHECKING")
                .getResultList().size();
        assertEquals("unexpected number of checking rows:" + rows, 1, rows);
        rows = em.createNativeQuery(
                "select ID, BALANCE, RATE from ORMINH_INTERESTACCT")
                .getResultList().size();
        assertEquals("unexpected number of interestacct rows:" + rows, 1, rows);
    }    
}
