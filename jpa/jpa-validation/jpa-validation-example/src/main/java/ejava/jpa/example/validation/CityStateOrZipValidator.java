package ejava.jpa.example.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * This type check will determine if city and state or zip where expressed for
 * an Address.
 */
public class CityStateOrZipValidator implements ConstraintValidator<CityStateOrZip, Address1>{

	@Override
	public void initialize(CityStateOrZip constraintAnnotation) {
	}

	@Override
	public boolean isValid(Address1 address, ConstraintValidatorContext context) {
		if (address==null) { return true; }
		return (address.getCity()!=null && address.getState()!=null) ||
				address.getZip()!=null;
	}

}
