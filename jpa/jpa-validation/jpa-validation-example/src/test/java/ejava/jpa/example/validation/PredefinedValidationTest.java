package ejava.jpa.example.validation;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.groups.Default;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;

/**
 * This test provides a demonstration of predefined constraints within the 
 * Validation API before we get to application-specific extensions.
 */
public class PredefinedValidationTest {
	private static Logger log = LoggerFactory.getLogger(PredefinedValidationTest.class);

	private ValidatorFactory vf = Validation.buildDefaultValidatorFactory();
	private Validator val = vf.getValidator();
	
	/**
	 * This test demonstrates a successful validation of all criteria
	 */
	@Test
	public void testValid() {
		log.info("*** testValid ***");

		Person p = new Person()
			.setFirstName("Billy Bob")
			.setLastName("Smith-Jones")
			.setBirthDate(new GregorianCalendar(1980, Calendar.JANUARY, 1).getTime())
			.setEmail("bbob@hotmail.com");
		
		Set<ConstraintViolation<Person>> violations = val.validate(p);
		
		log.debug("valid person=" + p);
		assertEquals("unexpected number of violations:" + violations, 0, violations.size());
	}

	/**
	 * This test demonstrates the use of the NotNull constraint
	 */
	@Test
	public void testNotNull() {
		log.info("*** testNotNull ***");

		Person p = new Person();
		
		Set<ConstraintViolation<Person>> violations = val.validate(p, POCs.class);
		for (ConstraintViolation<Person> v : violations) {
			log.info(v.getRootBeanClass() + ", " + v.getPropertyPath() + " " + v.getMessage());
		}
		
		log.debug("invalid null-named person=" + p);
		assertEquals("unexpected number of violations", 4, violations.size());
	}
	
	/**
	 * This test demonstrates the Size constraint
	 */
	@Test
	public void testSize() {
		log.info("*** testSize ***");
		
		Person p = new Person()
			.setFirstName("Bobbbbbbbbbbbbbbbbbbbbbbbbbbb")
			.setLastName("Smithhhhhhhhhhhhhhhhhhhhhhhhhh");
		
		Set<ConstraintViolation<Person>> violations = val.validate(p);
		for (ConstraintViolation<Person> v : violations) {
			log.info(v.getPropertyPath() + ":" + v.getInvalidValue() + " " + v.getMessage());
		}
		
		log.debug("invalid named-sized person=" + p);
		assertEquals("unexpected number of violations", 2, violations.size());
	}

	/**
	 * This test demonstrates the Pattern constraint
	 */
	@Test
	public void testPattern() {
		log.info("*** testPattern ***");
		
		Person p = new Person()
			.setFirstName("Bob2")
			.setLastName("Smith")
			.setEmail("bob2");
		
		Set<ConstraintViolation<Person>> violations = val.validate(p);
		for (ConstraintViolation<Person> v : violations) {
			log.info(v.getPropertyPath() + ":" + v.getInvalidValue() + " " + v.getMessage());
		}
		
		log.debug("invalid named-sized person=" + p);
		assertEquals("unexpected number of violations", 3, violations.size());
	}
	
	/**
	 * This test demonstrates the predefined Past constraint for dates
	 */
	@Test
	public void testPast() {
		log.info("*** testPast ***");
		
		Person p = new Person()
			.setFirstName("Bob")
			.setLastName("Smith")
			.setBirthDate(new GregorianCalendar(2052, Calendar.JANUARY, 1).getTime());
		
		Set<ConstraintViolation<Person>> violations = val.validate(p, Drivers.class);
		for (ConstraintViolation<Person> v : violations) {
			String value = (v.getInvalidValue() instanceof Date) ? 
					new SimpleDateFormat("YYYY").format((Date)v.getInvalidValue()) : 
					v.getInvalidValue().toString(); 
			log.info(v.getPropertyPath() + ":" + value + " " + v.getMessage());			
		}
		
		log.debug("invalid named-sized person=" + p);
		assertEquals("unexpected number of violations", 3, violations.size());
	}
	
	/**
	 * This test demonstrates the use of validation groups
	 */
	@Test
	public void testGroups() {
		log.info("*** testGroups ***");
		
		Calendar seventeen = new GregorianCalendar();
		seventeen.add(Calendar.YEAR, -17);
		
		Person p = new Person()
			.setFirstName("Bob")
			.setLastName("Smith")
			.setEmail("bob.smith@gmail.com")
			.setBirthDate(seventeen.getTime());
		
		Set<ConstraintViolation<Person>> validPerson = val.validate(p, Default.class);
		Set<ConstraintViolation<Person>> validDriver = val.validate(p, Drivers.class);
		Set<ConstraintViolation<Person>> validPOC = val.validate(p, POCs.class);
		
		log.debug(p.toString() + ", validPerson=" + validPerson.isEmpty() +
				                 ", validDriver=" + validDriver.isEmpty() +
				                 ", validPOC=" + validPOC.isEmpty());
		
		log.debug("validPerson=" + validPerson);
		log.debug("validDriver=" + validDriver);
		log.debug("validPOC=" + validPOC);
		
		assertTrue("not validPerson", validPerson.isEmpty());
		assertTrue("validDriver", validDriver.isEmpty());
		assertFalse("not validPOC", validPOC.isEmpty());
	}
	
}
