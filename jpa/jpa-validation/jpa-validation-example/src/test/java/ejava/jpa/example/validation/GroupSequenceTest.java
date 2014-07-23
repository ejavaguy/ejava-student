package ejava.jpa.example.validation;

import static org.junit.Assert.*;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.junit.Test;

/**
 * This test case demonstrates a validation using a group sequence. We can validate 
 * the specific groups -- but with this feature we can cause them to be called 
 * in a chain and stop if/when one of the groups fails without calling the downstream
 * validators.
 */
public class GroupSequenceTest {
	private static final Log log = LogFactory.getLog(GroupSequenceTest.class);
	private static final ValidatorFactory vf = Validation.buildDefaultValidatorFactory();
	private static final Validator val = vf.getValidator();

	/**
	 * This test will demonstrate validation stopping at the first group that 
	 * fails validation and not bother moving on to the next step.
	 */
	@Test
	public void testDefaultError() {
		log.debug("*** testDefaultError ***");
		
		Address1 a = new Address1();
		Set<ConstraintViolation<Address1>> violations = val.validate(a, ValidationSequence.class);
		for (ConstraintViolation<Address1> v: violations) {
			log.info(v.getPropertyPath() + ":" + v.getInvalidValue() + " " + v.getMessage());
		}
		
		//we should only get violations from the Default group
		assertEquals("unexpected number of violations", 4, violations.size());
	}

	/**
	 * This test demonstrates the validation will not move on from the default
	 * group since there are still a few failurs
	 */
	@Test
	public void testDefaultSomeError() {
		log.debug("*** testDefaultSomeError ***");
		
		Address1 a = new Address1()
		    .setStreet("$$%%^&#$$$$$$$$$$$$$$$$")
		    .setState("BIIIIIIIIIIIIIIIIIIIIG");
		Set<ConstraintViolation<Address1>> violations = val.validate(a, ValidationSequence.class);
		for (ConstraintViolation<Address1> v: violations) {
			log.info(v.getPropertyPath() + ":" + v.getInvalidValue() + " " + v.getMessage());
		}
		
		//we should only get violations from the Default group
		assertEquals("unexpected number of violations", 2, violations.size());
	}

	/**
	 * This test demonstrates the validation moving on to the second level
	 * of validation which deals with size and not content.
	 */
	@Test
	public void testOneDBError() {
		log.debug("*** testOneDBError ***");
		
		Address1 a = new Address1()
		    .setStreet("1600$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$")
		    .setCity("Washington")
		    .setState("DC")
		    .setZip("20500");
		Set<ConstraintViolation<Address1>> violations = val.validate(a, ValidationSequence.class);
		for (ConstraintViolation<Address1> v: violations) {
			log.info(v.getPropertyPath() + ":" + v.getInvalidValue() + " " + v.getMessage());
		}
		
		//we should only get violations from the DB group
		assertEquals("unexpected number of violations", 1, violations.size());
	}

	/**
	 * This test demonstrates validation moving onto the third level with 
	 * data content validation.
	 */
	@Test
	public void testDataError() {
		log.debug("*** testDataError ***");
		
		Address1 a = new Address1()
		    .setStreet("1600$")
		    .setCity("Washington")
		    .setState("DC")
		    .setZip("20500");
		Set<ConstraintViolation<Address1>> violations = val.validate(a, ValidationSequence.class);
		for (ConstraintViolation<Address1> v: violations) {
			log.info(v.getPropertyPath() + ":" + v.getInvalidValue() + " " + v.getMessage());
		}
		
		//we should only get violations from the Data Content group
		assertEquals("unexpected number of violations", 1, violations.size());
	}

	@Test
	public void testSuccess() {
		log.debug("*** testSuccess ***");
		
		Address1 a = new Address1()
		    .setStreet("1600")
		    .setCity("Washington")
		    .setState("DC")
		    .setZip("20500");
		Set<ConstraintViolation<Address1>> violations = val.validate(a, ValidationSequence.class);
		for (ConstraintViolation<Address1> v: violations) {
			log.info(v.getPropertyPath() + ":" + v.getInvalidValue() + " " + v.getMessage());
		}
		
		//we should get no violations
		assertEquals("unexpected number of violations", 0, violations.size());
	}
}
