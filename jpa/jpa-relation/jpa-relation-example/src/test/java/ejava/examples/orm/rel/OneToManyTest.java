package ejava.examples.orm.rel;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import ejava.examples.orm.rel.annotated.Borrower;
import ejava.examples.orm.rel.annotated.Checkout;
import ejava.examples.orm.rel.annotated.Person;

/**
 * This test case provides a demo of OneToMany and ManyToOne relationships.
 * In this type of relationship, the foreign key is either in the many side's
 * table or a link table.
 */
public class OneToManyTest extends DemoBase {
    private long borrowerId;
    
    /**
     * This setUp method creates a Borrower to be used during the test
     * methods.
     */
    @Before
    public void setUp() throws Exception {
        log.info("creating base Borrower for tests");
        ejava.examples.orm.rel.annotated.Person person = new Person();
        person.setFirstName("john");
        person.setLastName("smith");
        person.setPhone("1-800-555-1212");
        em.persist(person);
        
        ejava.examples.orm.rel.annotated.Borrower borrower = 
            new Borrower(person);
        borrower.setStartDate(new Date());
        em.persist(borrower);
        em.flush();
        em.clear();
        borrowerId = borrower.getId();
    }

    /**
     * This provides a basic demonstration of setting up a One/Many 
     * relationship. Take special note that because the entities of regular
     * POJOs and we no longer have CMR from EJB2.1, we must manually update the
     * in-memory references on both sides. However, it is only the setting() 
     * and persisting of the owning side of the relation that updates any 
     * foreign key relationships.
     *
     */
    @Test
    public void testManyOneBiDirectional() {
        log.info("testManyOneBiDirectional");
        //get a borrower
        log.info("getting borrower id=" + borrowerId);
        ejava.examples.orm.rel.annotated.Borrower borrower = 
            em.find(Borrower.class, borrowerId);
        assertNotNull(borrower);
        assertTrue(borrower.getCheckouts().size() == 0);
        
        //create 1st checkout
        ejava.examples.orm.rel.annotated.Checkout checkout = 
            new Checkout(new Date());
        checkout.setBorrower(borrower); //set owning side of the relation
        borrower.addCheckout(checkout); //set inverse side of relation
        em.persist(checkout);   //persist owning side of the relation
        log.info("added checkout to borrower:" + borrower);
        assertTrue(borrower.getCheckouts().size() == 1);
        log.info("here's checkout:" + checkout);
        
        //create a couple more
        for(int i=0; i<5; i++) {
            Checkout co = new Checkout(new Date());
            co.setBorrower(borrower);   //set owning side of the relation
            borrower.addCheckout(co);   //set inverse side of relation
            em.persist(co);  //persist owning side of the relation
        }
        log.info("done populating borrower");
        
        //see what we have - watch log to see when the ctor() of the LAZY
        // instantiated Checkouts occur. If after log.info, LAZY worked
        em.flush();
        em.clear();
        Borrower borrower2 = em.find(Borrower.class, borrower.getId());
        log.info("found borrower: " + borrower.getName());
        assertEquals(6, borrower2.getCheckouts().size());               
    }

    /**
     * This provides a basic demonstration of removing entities from a One/Many
     * relationship. Note that we have to update the java fields on both sides
     * of the bi-directional relationship.
     */
    @Test
    public void testRemove() {
        log.info("testRemove");
        //get a borrower
        log.info("getting borrower id=" + borrowerId);
        ejava.examples.orm.rel.annotated.Borrower borrower = 
            em.find(Borrower.class, borrowerId);
        assertNotNull(borrower);
        assertTrue(borrower.getCheckouts().size() == 0);
        
        //create a few checkouts
        for(int i=0; i<5; i++) {
            Checkout co = new Checkout(new Date());
            co.setBorrower(borrower);   //set the owning side of the relation
            em.persist(co);             //update the database
            borrower.addCheckout(co);   //update the inverse side in memory
        }
        log.info("populated borrower:" + borrower);
        assertEquals(5,borrower.getCheckouts().size());
        
        //start with borrower from DB and remove checkouts
        em.flush();
        em.clear();
        Borrower borrower2 = em.find(Borrower.class, borrowerId);
        assertNotNull(borrower2);
        log.info("found borrower:" + borrower2);
        assertEquals(5,borrower.getCheckouts().size());
        
        //remove checkouts
        for(Iterator<Checkout> itr = borrower2.getCheckouts().iterator(); 
            itr.hasNext();) { //note that the collection returned was internally
                              //copied by Borrower.getCheckouts()
            Checkout co = itr.next();
            borrower2.removeCheckout(co);  
            em.remove(co);                //remove from table
        }
        log.info("done removing checkouts from borrower:" + borrower2);
        assertEquals(0, borrower2.getCheckouts().size());

        //verify what is in database
        em.flush();
        em.clear();
        Borrower borrower3 = em.find(Borrower.class, borrowerId);
        assertNotNull(borrower3);
        log.info("found borrower:" + borrower3.getName());
        assertEquals(0,borrower3.getCheckouts().size());        
    }    
}
