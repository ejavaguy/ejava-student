package ejava.jpa.example.validation;

import static org.junit.Assert.*;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;

public class TypeValidatorTest {
	private static final Logger log = LoggerFactory.getLogger(TypeValidatorTest.class);
	ValidatorFactory cf = Validation.buildDefaultValidatorFactory();
	Validator val = cf.getValidator();
	
	/**
	 * This test provides a demonstration of validating multiple fields 
	 * across a single type.
	 */
	@Test
	public void testCityStateOrZip() {
		log.info("*** testCityStateOrZip ***");
		
		Address1 a1 = new Address1()
		    .setStreet("1600")
		    .setCity("Washington")
		    .setState("DC")
		    .setZip("20500");
		Set<ConstraintViolation<Address1>> violations = val.validate(a1,PreCheck.class);
		assertTrue("unexpected violation", violations.isEmpty());
	}

	@Test
	public void testCityState() {
		log.info("*** testCityState ***");
		
		Address1 a1 = new Address1()
		    .setStreet("1600")
		    .setCity("Washington")
		    .setState("DC");
		Set<ConstraintViolation<Address1>> violations = val.validate(a1,PreCheck.class);
		assertTrue("unexpected violation", violations.isEmpty());
	}
	
	@Test
	public void testCity() {
		log.info("*** testCity ***");
		
		Address1 a1 = new Address1()
		    .setStreet("1600")
		    .setCity("Washington");
		Set<ConstraintViolation<Address1>> violations = val.validate(a1,PreCheck.class);
		for (ConstraintViolation<Address1> v : violations) {
			log.debug(v.getPropertyPath() + ":" + v.getInvalidValue() + ", " + v.getMessage());
		}
		assertEquals("unexpected violation", 1, violations.size());
	}
	
	@Test
	public void testState() {
		log.info("*** testState ***");
		
		Address1 a1 = new Address1()
		    .setStreet("1600")
		    .setState("DC");
		Set<ConstraintViolation<Address1>> violations = val.validate(a1,PreCheck.class);
		for (ConstraintViolation<Address1> v : violations) {
			log.debug(v.getPropertyPath() + ":" + v.getInvalidValue() + ", " + v.getMessage());
		}
		assertEquals("unexpected violation", 1, violations.size());
	}

	@Test
	public void testZip() {
		log.info("*** testCityStateOrZip ***");
		
		Address1 a1 = new Address1()
		    .setStreet("1600")
		    .setZip("20500");
		Set<ConstraintViolation<Address1>> violations = val.validate(a1,PreCheck.class);
		assertTrue("unexpected violation", violations.isEmpty());
	}
}
