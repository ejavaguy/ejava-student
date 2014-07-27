package ejava.examples.orm.rel;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ejava.examples.orm.rel.annotated.Applicant;
import ejava.examples.orm.rel.annotated.Borrower;
import ejava.examples.orm.rel.annotated.Person;

/**
 * This test case provides a demo of a ManyToMany relationship. This,
 * of course, also uses a Join (or link) table.
 */
public class RelationshipOwnershipTest extends DemoBase {
    private long borrowerId;
    private long applicantId;
    
    /**
     * This setUp method creates a few objects that we'll use to test a few
     * relations.
     */
    @Before
    public void setUp() throws Exception {        
        ejava.examples.orm.rel.annotated.Person person = new Person();
        person.setFirstName("test");
        person.setLastName("one2oneOwnership");
        em.persist(person);
        
        ejava.examples.orm.rel.annotated.Applicant applicant = new Applicant();
        applicant.setIdentity(person);
        em.persist(applicant);
        
        ejava.examples.orm.rel.annotated.Borrower borrower = 
            new Borrower(person);
        em.persist(borrower);
        
        em.getTransaction().commit();
        borrowerId = borrower.getId();
        applicantId = applicant.getId();
        
        em.getTransaction().begin();
    }

    /**
     * This method makes sure there are no relationships left over after test
     */
    @After
    public void tearDown() throws Exception {        
        
        Borrower borrower = em.find(Borrower.class, borrowerId);
        Applicant applicant = em.find(Applicant.class, applicantId);
        em.remove(borrower);
        em.remove(applicant);
        
        if (!em.getTransaction().isActive()) {
            em.getTransaction().begin();
            em.getTransaction().commit();
        }
    }

    /**
     * This method will verify that setting only the inverse side of a 
     * OnetoOne relationship does not update the database. In this 
     * particular case the Application table contains the foreign key to
     * the Borrower, so an update of only the Borrower object does not get
     * reflected in the DB when the transaction commits.
     *
     */
    @Test
    public void testOnetoOneInverse() {
        log.info("testOneToOneInverse");
        
        Borrower borrower = em.find(Borrower.class, borrowerId);
        Applicant applicant = em.find(Applicant.class, applicantId);

        assertNull("borrower has unexpected applicant:" + 
                borrower.getApplication(), 
                borrower.getApplication());
        assertNull("applicant has unexpected borrower:" + 
                applicant.getBorrower(), 
                applicant.getBorrower());

        //set ONLY the inverse side of the relationship
        borrower.setApplication(applicant);
        assertNotNull("borrower does not have applicant", 
                borrower.getApplication());
        assertNull("applicant has unexpected borrower:" + 
                applicant.getBorrower(), 
                applicant.getBorrower());  
        
        log.info("writing rel owner (application) to DB:" + applicant);
        log.info("writing rel inverse (borrower) to DB:" + borrower);

        //commit changes to the DB, but since only inserse side of relationship
        //was set, no FK data gets written
        em.getTransaction().commit();
        em.clear();

        assertFalse("borrower was managed", em.contains(borrower));
        assertFalse("application was managed", em.contains(applicant));
        
        borrower = em.find(Borrower.class, borrowerId);
        applicant = em.find(Applicant.class, applicantId);
        log.info("read rel owner (application) from DB:" + applicant);
        log.info("read rel inverse (borrower) from DB:" + borrower);

        //verify that relationship from cache never written to DB
        assertNull("borrower has unexpected applicant:" + 
                borrower.getApplication(), 
                borrower.getApplication());
        assertNull("applicant has unexpected borrower:" + 
                applicant.getBorrower(), 
                applicant.getBorrower());
        
        //wire both sides of the relationship and write to DB
        borrower.setApplication(applicant);
        applicant.setBorrower(borrower);
        em.getTransaction().begin(); 
        em.getTransaction().commit();        
        em.clear();

        assertFalse("borrower was managed", em.contains(borrower));
        assertFalse("application was managed", em.contains(applicant));
        
        borrower = em.find(Borrower.class, borrowerId);
        applicant = em.find(Applicant.class, applicantId);
        log.info("read rel owner (application) from DB:" + applicant);
        log.info("read rel inverse (borrower) from DB:" + borrower);

        //verify that relationship written to DB
        assertNotNull("borrower doesn't have applicant", 
                borrower.getApplication());
        assertNotNull("applicant doesn't have borrower", 
                applicant.getBorrower());
        
        //remove only inverse side of relationship
        borrower.setApplication(null);
        assertNull("borrower has unexpected applicant:" + 
                borrower.getApplication(), 
                borrower.getApplication());
        assertNotNull("applicant does not have borrower", 
                applicant.getBorrower());
        
        log.info("writing rel owner (application) to DB:" + applicant);
        log.info("writing rel inverse (borrower) to DB:" + borrower);

        //commit changes to the DB, but since only inserse side of relationship
        //was null, the FK won't get reset
        em.getTransaction().begin();
        em.getTransaction().commit();        
        em.clear();

        
        assertFalse("borrower was managed", em.contains(borrower));
        assertFalse("application was managed", em.contains(applicant));
        
        borrower = em.find(Borrower.class, borrowerId);
        applicant = em.find(Applicant.class, applicantId);
        log.info("read rel owner (application) from DB:" + applicant);
        log.info("read rel inverse (borrower) from DB:" + borrower);

        //verify that relationship from cache never written to DB
        assertNotNull("borrower does not have applicant", 
                borrower.getApplication());
        assertNotNull("applicant does not have borrower", 
                applicant.getBorrower());
   }
    
    /**
     * This method will verify that setting only the owning side of a 
     * OnetoOne relationship does update the database, but leaves the cache
     * in an inconsistent state until refreshed. In this 
     * particular case the Application table contains the foreign key to
     * the Borrower. A change to Applicant gets written to DB, but not 
     * automatically reflected in Borrower until manually set of refreshed.
     */
    @Test
    public void testOnetoOneOwnership() {
        log.info("testOneToOneOwnership");
        
        Borrower borrower = em.find(Borrower.class, borrowerId);
        Applicant applicant = em.find(Applicant.class, applicantId);

        assertNull("borrower has unexpected applicant:" + 
                borrower.getApplication(), 
                borrower.getApplication());
        assertNull("applicant has unexpected borrower:" + 
                applicant.getBorrower(), 
                applicant.getBorrower());

        //set ONLY the owning side of the relationship
        applicant.setBorrower(borrower);
        assertNull("borrower has unexpected applicant:" + 
                borrower.getApplication(), 
                borrower.getApplication());
        assertNotNull("applicant does not have borrower", 
                applicant.getBorrower());

        //commit changes to the DB, since the owning side was set, we do
        //get changes made to DB
        em.getTransaction().commit();
        em.clear();
        
        borrower = em.find(Borrower.class, borrowerId);
        applicant = em.find(Applicant.class, applicantId);

        //verify that relationship from cache written to DB
        assertNotNull("borrower was not updated with applicant:" + 
                borrower.getApplication(), 
                borrower.getApplication());
        assertNotNull("applicant was not updated with borrower", 
                applicant.getBorrower());
        

        //remove relationship from borrower
        applicant.setBorrower(null);
        log.info("writing rel owner (application) to DB:" + applicant);
        log.info("writing rel inverse (borrower) to DB:" + borrower);
        
        em.getTransaction().begin();
        em.getTransaction().commit();

        log.info("refreshing stale borrower:" + borrower);
        em.refresh(borrower);
        log.info("stale borrower refreshed:" + borrower);

        assertNull("borrower has unexpected applicant:" +
                borrower.getApplication(), 
                borrower.getApplication());
    }    
}
