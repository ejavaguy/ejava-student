package ejava.examples.orm.rel;

import org.junit.Test;

import ejava.examples.orm.rel.annotated.Borrower;
import ejava.examples.orm.rel.annotated.Library;
import ejava.examples.orm.rel.annotated.Person;

/**
 * This test case provides a demo of a OneToMany relationship using a Map
 * In this relationship, a field from the related object is used to populate 
 * the key of a Map entry.
 */
public class OneToManyMapTest extends DemoBase {
    
    @Test
    public void testMapCreate() {
        log.info("testMapCreate");
        ejava.examples.orm.rel.annotated.Library library
            = new Library();
        
        em.persist(library);
        
        for(int i=0; i<5; i++) {
            ejava.examples.orm.rel.annotated.Person person =
                new Person();
            person.setFirstName("test");
            person.setLastName("MapCreate-" + i);
            em.persist(person); //persist now because borrower copies id
            ejava.examples.orm.rel.annotated.Borrower borrower = 
                new Borrower(person);
            log.info("created borrower:" + borrower);
            library.getBorrowers().put(borrower.getId(), borrower);
            log.info("added borrower to library:" + library);
        }
        log.info("creating inventory:" + library);
    }    
}
