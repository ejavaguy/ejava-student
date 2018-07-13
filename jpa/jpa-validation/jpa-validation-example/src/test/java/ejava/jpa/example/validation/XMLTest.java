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
 * This test case demonstrates the use of deployment descriptors to define
 * validation for beans.
 */
public class XMLTest {
	private static final Logger log = LoggerFactory.getLogger(XMLTest.class);
	
	ValidatorFactory vf = Validation.buildDefaultValidatorFactory();
	Validator val = vf.getValidator();

	/**
	 * This test verifies the XML-based definitions will be used to 
	 * detect a validation error with the bean.
	 */
	@Test
	public void testValidationDescriptorBad() {
		log.info("*** testValidationDescriptorBad");
		
		Book b = new Book();
		Set<ConstraintViolation<Book>> violations = val.validate(b);
		for (ConstraintViolation<Book> v : violations) {
			log.info(v.getPropertyPath() + ":" + v.getInvalidValue() + " " + v.getMessage());
		}
		assertEquals("unexpected number of violations", 2, violations.size());
	}

	/**
	 * This test verifies the XML-based definitions will successfully validate
	 * and pass a valid bean. 
	 */
	@Test
	public void testValidationDescriptorGood() {
		log.info("*** testValidationDescriptorGood");
		
		Book b = new Book().setTitle("Validation Rocks!").setPages(30);
		Set<ConstraintViolation<Book>> violations = val.validate(b);
		assertEquals("unexpected number of violations", 0, violations.size());
	}
}
