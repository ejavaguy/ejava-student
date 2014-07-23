package ejava.jpa.example.validation;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MinAgeValidator implements ConstraintValidator<MinAge, Date>{
	int minAge;

	@Override
	public void initialize(MinAge constraint) {
		this.minAge = constraint.age();
	}

	@Override
	public boolean isValid(Date date, ConstraintValidatorContext ctx) {
		if (date==null) { return true; }
		//get today's date
		Calendar latestBirthDate = new GregorianCalendar();
		//determine min date that meets age requirement
		latestBirthDate.add(Calendar.YEAR, -1*minAge);
		
		//get calendate date of object
		Calendar birthDate = new GregorianCalendar();
		birthDate.setTime(date);

		//return whether meets criteria
		if (birthDate.after(latestBirthDate)) {
			String errorMsg = String.format("%d is younger than minimum %d", 
					getAge(birthDate), 
					minAge);
			ctx.buildConstraintViolationWithTemplate(errorMsg)
				.addConstraintViolation();
			return false;
		} else {
			return true;
		}
	}
	
	private int getAge(Calendar birth) {
		Calendar now = new GregorianCalendar();
		int years = now.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
		int months = now.get(Calendar.MONTH) - birth.get(Calendar.MONTH);
		if (months < 0) { years -= 1; }
		else if (months==0) {
			int days = now.get(Calendar.DAY_OF_YEAR) - birth.get(Calendar.DAY_OF_YEAR);
			if (days < 0) { years -= 1; }
		}
		return years;
	}
}
