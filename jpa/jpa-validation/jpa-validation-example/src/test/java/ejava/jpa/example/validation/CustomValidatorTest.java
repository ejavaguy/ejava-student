package ejava.jpa.example.validation;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;

public class CustomValidatorTest {
	private static final Logger logger = LoggerFactory.getLogger(CustomValidatorTest.class);

	private ValidatorFactory vf = Validation.buildDefaultValidatorFactory();
	private Validator val = vf.getValidator();
	
	/**
	 * This test demonstrates the custom @MinAge validation for the Drivers
	 * group.
	 */
	@Test
	public void testMinAgeValid() {
		logger.info("*** testMinAgeValid ***");
		
		Calendar sixteen = new GregorianCalendar();
		sixteen.add(Calendar.YEAR, -16);
		
		Person p = new Person()
			.setFirstName("Bob")
			.setLastName("Smith")
			.setBirthDate(sixteen.getTime());
	
		Set<ConstraintViolation<Person>> violations = val.validate(p, Drivers.class);
		logger.debug(p + ", violations=" + violations);
		assertTrue("not valid driver", violations.isEmpty());
	}

	@Test
	public void testMinAgeInValid() {
		logger.info("*** testMinAgeInvalid ***");
		
		Calendar fifteen = new GregorianCalendar();
		fifteen.add(Calendar.YEAR, -16);
		fifteen.add(Calendar.DAY_OF_YEAR, 2);
		
		Person p = new Person()
			.setFirstName("Bob")
			.setLastName("Smith")
			.setBirthDate(fifteen.getTime());
	
		Set<ConstraintViolation<Person>> violations = val.validate(p, Drivers.class);
		for (ConstraintViolation<Person> v : violations) {
			logger.info("{}:{} {}", v.getPropertyPath(), v.getInvalidValue(), v.getMessage());
		}

		logger.debug(p + ", violations=" + violations);
		assertFalse("valid driver", violations.isEmpty());
	}

	@Test
	public void testComposite() {
		logger.info("*** testComposite ***");
		
		Person p = new Person()
			.setFirstName("Bob")
			.setLastName("Smithhhhhhhhhhhhhhhhhh$%$%$$$$$$$$$$$$$$$$");
	
		Set<ConstraintViolation<Person>> violations = val.validate(p);
		for (ConstraintViolation<Person> v : violations) {
			logger.info("{}:{} {}", v.getPropertyPath(), v.getInvalidValue(), v.getMessage());
		}

		logger.debug(p + ", violations=" + violations);
		assertFalse("valid driver", violations.isEmpty());
	}
}
