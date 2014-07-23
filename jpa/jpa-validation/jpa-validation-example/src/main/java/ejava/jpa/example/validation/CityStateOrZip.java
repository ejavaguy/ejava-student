package ejava.jpa.example.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Defines a type constraint annotation for expressing an address must have
 * either city&state or zip code expressed.
 */
@Documented
@Constraint(validatedBy={CityStateOrZipValidator.class})
@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface CityStateOrZip {
	String message() default "must have city and state or zip code";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default{};
	
	/**
	 * Defines an array of annotations so that more than one can be applied.
	 */
	@Target({TYPE, ANNOTATION_TYPE})
	@Retention(RUNTIME)
	@Documented
	public @interface List {
		CityStateOrZip[] value();
	} 
}
