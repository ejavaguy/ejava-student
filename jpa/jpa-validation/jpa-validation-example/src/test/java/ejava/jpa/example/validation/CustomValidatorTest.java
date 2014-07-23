package ejava.jpa.example.validation;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

public class CustomValidatorTest {
	private static final Log log = LogFactory.getLog(CustomValidatorTest.class);

	private ValidatorFactory vf = Validation.buildDefaultValidatorFactory();
	private Validator val = vf.getValidator();
	
	/**
	 * This test demonstrates the custom @MinAge validation for the Drivers
	 * group.
	 */
	@Test
	public void testMinAgeValid() {
		log.info("*** testMinAgeValid ***");
		
		Calendar sixteen = new GregorianCalendar();
		sixteen.add(Calendar.YEAR, -16);
		
		Person p = new Person()
			.setFirstName("Bob")
			.setLastName("Smith")
			.setBirthDate(sixteen.getTime());
	
		Set<ConstraintViolation<Person>> violations = val.validate(p, Drivers.class);
		log.debug(p + ", violations=" + violations);
		assertTrue("not valid driver", violations.isEmpty());
	}

	@Test
	public void testMinAgeInValid() {
		log.info("*** testMinAgeInvalid ***");
		
		Calendar fifteen = new GregorianCalendar();
		fifteen.add(Calendar.YEAR, -16);
		fifteen.add(Calendar.DAY_OF_YEAR, 2);
		
		Person p = new Person()
			.setFirstName("Bob")
			.setLastName("Smith")
			.setBirthDate(fifteen.getTime());
	
		Set<ConstraintViolation<Person>> violations = val.validate(p, Drivers.class);
		for (ConstraintViolation<Person> v : violations) {
			log.debug(v.getPropertyPath() + ":" + v.getInvalidValue() + ", " + v.getMessage());
		}

		log.debug(p + ", violations=" + violations);
		assertFalse("valid driver", violations.isEmpty());
	}

	@Test
	public void testComposite() {
		log.info("*** testComposite ***");
		
		Person p = new Person()
			.setFirstName("Bob")
			.setLastName("Smithhhhhhhhhhhhhhhhhh$%$%$$$$$$$$$$$$$$$$");
	
		Set<ConstraintViolation<Person>> violations = val.validate(p);
		for (ConstraintViolation<Person> v : violations) {
			log.debug(v.getPropertyPath() + ":" + v.getInvalidValue() + " " + v.getMessage());
		}

		log.debug(p + ", violations=" + violations);
		assertFalse("valid driver", violations.isEmpty());
	}
}
