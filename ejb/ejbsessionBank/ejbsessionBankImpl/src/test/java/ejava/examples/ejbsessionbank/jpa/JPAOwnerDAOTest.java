package ejava.examples.ejbsessionbank.jpa;

import java.util.Collection;

import static org.junit.Assert.*;
import java.util.HashMap;
import java.util.List;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;

import ejava.examples.ejbsessionbank.DemoBase;
import ejava.examples.ejbsessionbank.bo.Account;
import ejava.examples.ejbsessionbank.bo.Owner;
import ejava.examples.ejbsessionbank.dao.AccountDAO;
import ejava.examples.ejbsessionbank.dao.OwnerDAO;

public class JPAOwnerDAOTest extends DemoBase {
    private Logger log = LoggerFactory.getLogger(JPAOwnerDAO.class);
    protected OwnerDAO ownerDAO;
    protected AccountDAO accountDAO;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        
        ownerDAO = new JPAOwnerDAO();
        ((JPAOwnerDAO)ownerDAO).setEntityManager(em);
        
        accountDAO = new JPAAccountDAO();
        ((JPAAccountDAO)accountDAO).setEntityManager(em);
    }

    @Test
    public void testCreateOwner() {
        log.info("*** testCreateOwner ***");
        
        Owner owner = new Owner();
        owner.setFirstName("john");
        owner.setLastName("jones");
        owner.setSsn("111");
        
        ownerDAO.createOwner(owner);
        em.getTransaction().commit();
        em.clear();
        
        Owner owner2 = ownerDAO.getOwnerById(owner.getId());
        assertEquals("unexpected firstName", 
                owner.getFirstName(), owner2.getFirstName());
                
        Account account = new Account();
        account.setAccountNumber("000111");
        account.deposit(5.00);
        owner2.getAccounts().add(account);
        
        account = new Account();
        account.setAccountNumber("000222");
        account.deposit(10.00);
        owner2.getAccounts().add(account);        
        
        em.getTransaction().begin();
        em.getTransaction().commit();
        em.clear();
        
        Owner owner3 = ownerDAO.getOwnerById(owner.getId());
        assertEquals("unexpected number of accounts", 
                2, owner3.getAccounts().size());
        account = accountDAO.getAccountById(account.getId());
        
        
        for (int i=0; i<100; i++) {
            Owner owner4 = new Owner();
            owner4.setFirstName("fn." + i);
            owner4.setLastName("sn." + i);
            owner4.setSsn("ssn." + i);
            owner4.getAccounts().add(account);
            ownerDAO.createOwner(owner4);
        }
        em.getTransaction().begin();
        em.getTransaction().commit();
        em.clear();
        
        Collection<Owner> owners = 
            ownerDAO.findOwners(OwnerDAO.GET_OWNERS_QUERY, null, 0, 1000);
        assertEquals("unexpected number of owners found", 101, owners.size());
        for(Owner o : owners) {
            if (o.getId() == owner.getId()) {
                assertEquals("unexpected number of accounts for owner",
                        2, o.getAccounts().size());
            }
            else {
                assertEquals("unexpected number of accounts for owner",
                        1, o.getAccounts().size());
            }
        }
        
        
        int count = accountDAO.findAccounts(
                    AccountDAO.GET_ACCOUNTS_QUERY, null, 0, 1000).size();
        assertEquals("unexpected number of accounts", 2, count);
        
        em.getTransaction().begin();
        ownerDAO.removeOwner(owner);
        em.getTransaction().commit();
        em.clear();
                
        for(Owner o : owners) {
            count = accountDAO.findAccounts(
                    AccountDAO.GET_ACCOUNTS_QUERY, null, 0, 1000).size();
            assertEquals("unexpected number of accounts", 1, count);

            em.getTransaction().begin();
            ownerDAO.removeOwner(o);
            em.getTransaction().commit();
        }
        
        count = accountDAO.findAccounts(
                AccountDAO.GET_ACCOUNTS_QUERY, null, 0, 1000).size();
        assertEquals("unexpected number of accounts", 0, count);
    }

    @Test
    public void testGetAuthor() throws Exception {
        Owner owner = new Owner();
        owner.setFirstName("johnx");
        owner.setLastName("jonesx");
        owner.setSsn("111x");        
        ownerDAO.createOwner(owner);

        Account account = new Account();
        account.setAccountNumber("000111x");
        owner.getAccounts().add(account);
        em.getTransaction().commit();
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("account", account);
        Collection<Owner> owners = 
        	ownerDAO.findOwners("getAccountOwner", params, 0, 100);
        assertNotNull("null owners", owners);
        assertEquals("unexpected number of owners", 1, owners.size());
        List<Owner> owners2 = ownerDAO.getAccountOwners(account);
        assertNotNull("null owners", owners2);
        assertEquals("unexpected number of owners2", 1, owners2.size());
        
        em.getTransaction().begin();
        ownerDAO.removeOwner(owners2.get(0));
        accountDAO.removeAccount(account);
        em.getTransaction().commit();
    }

}
