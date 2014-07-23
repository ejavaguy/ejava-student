package ejava.jpa.example.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Defines a constraint annotation for expressing a minimum age.
 */
@Documented
@Constraint(validatedBy={MinAgeValidator.class})
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Retention(RUNTIME)
public @interface MinAge {
	String message() default "too young";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default{};
	int age();
	
	/**
	 * Defines an array of annotations so that more than one can be applied.
	 */
	@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
	@Retention(RUNTIME)
	@Documented
	public @interface List {
		MinAge[] value();
	} 
}
