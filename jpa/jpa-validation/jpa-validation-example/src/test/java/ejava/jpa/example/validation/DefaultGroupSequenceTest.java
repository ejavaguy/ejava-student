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

/**
 * This test case demonstrates a class that has had its Default validation replaced
 * by a group sequence.
 */
public class DefaultGroupSequenceTest {
	private static final Logger logger = LoggerFactory.getLogger(DefaultGroupSequenceTest.class);
	private static final ValidatorFactory vf = Validation.buildDefaultValidatorFactory();
	private static final Validator val = vf.getValidator();

	/**
	 * This test will demonstrate validation stopping at the first group that 
	 * fails validation and not bother moving on to the next step.
	 */
	@Test
	public void testDefaultError() {
		logger.debug("*** testDefaultError ***");
		
		Address2 a = new Address2();
		Set<ConstraintViolation<Address2>> violations = val.validate(a);
		for (ConstraintViolation<Address2> v: violations) {
			logger.info("{}:{} {}", v.getPropertyPath(), v.getInvalidValue(), v.getMessage());
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
		logger.debug("*** testDefaultSomeError ***");
		
		Address2 a = new Address2()
		    .setStreet("$$%%^&#$$$$$$$$$$$$$$$$")
		    .setState("BIIIIIIIIIIIIIIIIIIIIG");
		Set<ConstraintViolation<Address2>> violations = val.validate(a);
		for (ConstraintViolation<Address2> v: violations) {
            logger.info("{}:{} {}", v.getPropertyPath(), v.getInvalidValue(), v.getMessage());
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
		logger.debug("*** testOneDBError ***");
		
		Address2 a = new Address2()
		    .setStreet("1600$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$")
		    .setCity("Washington")
		    .setState("DC")
		    .setZip("20500");
		Set<ConstraintViolation<Address2>> violations = val.validate(a);
		for (ConstraintViolation<Address2> v: violations) {
            logger.info("{}:{} {}", v.getPropertyPath(), v.getInvalidValue(), v.getMessage());
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
		logger.debug("*** testDataError ***");
		
		Address2 a = new Address2()
		    .setStreet("1600$")
		    .setCity("Washington")
		    .setState("DC")
		    .setZip("20500");
		Set<ConstraintViolation<Address2>> violations = val.validate(a);
		for (ConstraintViolation<Address2> v: violations) {
            logger.info("{}:{} {}", v.getPropertyPath(), v.getInvalidValue(), v.getMessage());
		}
		
		//we should only get violations from the Data Content group
		assertEquals("unexpected number of violations", 1, violations.size());
	}

	@Test
	public void testSuccess() {
		logger.debug("*** testSuccess ***");
		
		Address2 a = new Address2()
		    .setStreet("1600")
		    .setCity("Washington")
		    .setState("DC")
		    .setZip("20500");
		Set<ConstraintViolation<Address2>> violations = val.validate(a);
		for (ConstraintViolation<Address2> v: violations) {
            logger.info("{}:{} {}", v.getPropertyPath(), v.getInvalidValue(), v.getMessage());
		}
		
		//we should get no violations
		assertEquals("unexpected number of violations", 0, violations.size());
	}
}
