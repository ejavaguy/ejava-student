package ejava.examples.txhotel.jpa;

import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.persistence.OptimisticLockException;

import ejava.examples.txhotel.bo.Person;
import ejava.examples.txhotel.bo.Reservation;

/**
 * This class provides a demonstration of Optimistic Locking as implemented
 * by javax.peristence. Both Person and Reservation have a version field;
 * only Reservation.version is known to the provider as an official version
 * field. You'll note below that the provider stops us from updating an 
 * object in the database with a detached object that contains a version
 * different from what is in the database.
 *
 */
public class OptimisticLockingTest extends DAOTestBase {

    /**
     * Person has a version field, but it is not registered with the provider.
     * Last one to make a change always wins.
     *
     */
    public void testChangeNoVersion() {
        log.info("*** testChangeNoVersion ***");
        
        long version=0;
        
        Person person = new Person(0,version, "joe", "jones");

        em.persist(person);
        log.debug("persisted person:" + person);
        assertEquals(version,person.getVersion());

        em.flush();
        log.debug("flushed:" + person);
        assertEquals(version,person.getVersion());

        em.getTransaction().commit();
        em.clear();
        em.getTransaction().begin();
        
        person.setFirstName("joey");
        log.debug("updated person:" + person);
        person = em.merge(person);
        log.debug("merged with correct version:" + person);
        assertEquals(version,person.getVersion());
        
        em.getTransaction().commit();
        em.clear();
        em.getTransaction().begin();
        
        person.setVersion(99);
        person.setFirstName("joseph");
        person = em.merge(person);
        log.debug("merged with incorrect version:" + person);
        assertEquals(99,person.getVersion());
    }
    
    /**
     * Reservation's version field is registered with the provider. Only 
     * detached objects with the matching version number can make an update.
     * All others will fail.
     *
     */
    public void testChangeWithVersion() {
        log.info("*** testChangeWithVersion ***");
        
        long version=0;
        
        Person person = new Person(0,version, "joe", "jones");
        Calendar start = new GregorianCalendar();
        start.add(Calendar.DAY_OF_YEAR, 10);
        Calendar end = new GregorianCalendar();
        start.setTime(start.getTime());
        start.add(Calendar.DAY_OF_YEAR, 14);
        Reservation reservation = new Reservation(
                0,version, "1234", person, start.getTime(), end.getTime());

        em.persist(reservation);
        log.debug("persisted reservation:" + reservation);
        assertEquals(version,reservation.getVersion());

        em.flush();
        log.debug("flushed:" + reservation);
        assertEquals(version,reservation.getVersion());
                
        em.getTransaction().commit();
        em.clear();
        em.getTransaction().begin();
                
        start.add(Calendar.DAY_OF_YEAR, 1);
        reservation.setStartDate(start.getTime());
        log.debug("updated reservation:" + reservation);
        reservation = em.merge(reservation);        
        em.flush();
        log.debug("merged valid version:" + reservation);
        assertEquals(++version,reservation.getVersion());

        em.getTransaction().commit();
        em.clear();
        em.getTransaction().begin();                
                
        reservation.setVersion(version - 1);
        start.add(Calendar.DAY_OF_YEAR, 1);
        reservation.setStartDate(start.getTime());
        log.debug("trying to change:" + reservation);
        try {
            reservation = em.merge(reservation);
            fail("failed to check version");
        }
        catch (OptimisticLockException ex) {
            log.debug("caught expected exception:" + ex);            
        }
        log.debug("finished with local:" + reservation);
        assertEquals(version-1,reservation.getVersion());
        reservation = em.find(Reservation.class, reservation.getId());
        log.debug("finished with db:" + reservation);
        assertEquals(version,reservation.getVersion());
    }
}
