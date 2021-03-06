package ejava.jpa.example.validation;

import java.util.Calendar;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;

/**
 * This test case demonstrates the integration of the validation API with
 * JPA
 */
public class JPAValidation extends JPATestBase {
	private static final Logger logger = LoggerFactory.getLogger(JPAValidation.class);

	/**
	 * This test demonstrates we can persist and update an entity with 
	 * valid values.
	 */
	@Test
	public void testPersistValidPerson() {
		logger.info("*** testPersistValidPerson ***");
		
		Person p = new Person()
		    .setFirstName("Bob")
		    .setLastName("Smith")
		    .setBirthDate(new GregorianCalendar(1980, Calendar.JANUARY, 1).getTime());
		em.persist(p);
		em.flush();
		
		p.setBirthDate(new GregorianCalendar(1980, Calendar.MAY, 1).getTime());
		em.flush();
	}
	
	/**
	 * This test demonstrates we cannot persist an entity with invalid values.
	 */
	@Test
	public void testPersistInValidPerson() {
		logger.info("*** testPersistInValidPerson ***");
		
		Person p = new Person()
		    .setFirstName("Bob")
		    .setLastName("Smith")
		    .setBirthDate(new Date());
		try {
			em.persist(p);
			em.flush(); //trigger the validation
		} catch (ConstraintViolationException ex) {
			logger.info("caught expected exception:" + ex);
		}
	}
	
	/**
	 * This test demonstrates we cannot update an entity with invalid values
	 */
	@Test
	public void testUpdatetInValidPerson() {
		logger.info("*** testUpdatetInValidPerson ***");
		
		Person p = new Person()
		    .setFirstName("Bob")
		    .setLastName("Smith")
		    .setBirthDate(new GregorianCalendar(1980, Calendar.JANUARY, 1).getTime());
		em.persist(p);
		em.flush();
		
		try {
			logger.debug("nulling birth date");			
			p.setBirthDate(null);
			logger.debug("flushing changes");
			em.flush();			
		} catch (ConstraintViolationException ex) {
			logger.info("caught expected exception:" + ex);
		}
	}
	
	
}
