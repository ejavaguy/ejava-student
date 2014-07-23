package ejava.jpa.example.validation;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.junit.Test;

/**
 * This test case is used to demonstrate the ability to cascade validation
 * across relationships for both simple POJOs and JPA entities.
 */
public class CascadeTest extends JPATestBase {
	private static final Log log = LogFactory.getLog(CascadeTest.class);
	
	ValidatorFactory vf = Validation.buildDefaultValidatorFactory();
	Validator val = vf.getValidator();

	/**
	 * This tests validation of two related POJOs. The parent is valid and
	 * two of the children have 1 violation each. We expect the validation
	 * to be able to cascade across the relationship and validate both the 
	 * parent and child beans.
	 */
	@Test
	public void testPOJOCascade() {
		log.info("*** testPOJOCascade ***");
		
		Purchase p = new Purchase()
			.setDate(new GregorianCalendar(2013, Calendar.JANUARY, 1).getTime()) //making sure in past
			.addItem(new PurchaseItem()
						.setAmount(new BigDecimal(60))
						.setCount(2))
			.addItem(new PurchaseItem()
						.setDescription("shoes")
						.setCount(2));
		Set<ConstraintViolation<Purchase>> violations = val.validate(p);
		for (ConstraintViolation<Purchase> v: violations) {
			log.info(v.getPropertyPath() + ":" + v.getInvalidValue() + " " + v.getMessage());
		}
		assertEquals("unexpected number of violations", 2, violations.size());
	}
	
	@Test
	public void testJPACascade() {
		log.info("*** testJPACascade ***");
		
		Purchase p = new Purchase()
					.setDate(new Date());
		PurchaseItem item1 = new PurchaseItem()
					.setAmount(new BigDecimal(60))
					.setCount(2);
		PurchaseItem item2 = new PurchaseItem()
					.setDescription("hammer")
					.setCount(1);
		p.addItem(item1).addItem(item2);
		try {
			em.persist(p);
		} catch (ConstraintViolationException ex) {
			for (ConstraintViolation<?> v: ex.getConstraintViolations()) {
				log.info(v.getPropertyPath() + ":" + v.getInvalidValue() + " " + v.getMessage());
			}
		}
	}

}
