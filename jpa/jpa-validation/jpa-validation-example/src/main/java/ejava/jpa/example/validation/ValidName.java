package ejava.jpa.example.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;
import javax.validation.Constraint;
import javax.validation.OverridesAttribute;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Defines a validation composition
 */
@NotNull
@Size
@Pattern(regexp="")
@ReportAsSingleViolation

@Documented
@Constraint(validatedBy={})
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Retention(RUNTIME)
public @interface ValidName {
	String message() default "invalid name";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default{};
	@OverridesAttribute(constraint=Size.class, name="min") int min() default 0;
	@OverridesAttribute(constraint=Size.class, name="max") int max() default Integer.MAX_VALUE;
	@OverridesAttribute(constraint=Pattern.class, name="regexp") String regexp() default ".*";

	/**
	 * Defines an array of annotations so that more than one can be applied.
	 */
	@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
	@Retention(RUNTIME)
	@Documented
	public @interface List {
		ValidName[] value();
	} 
}
