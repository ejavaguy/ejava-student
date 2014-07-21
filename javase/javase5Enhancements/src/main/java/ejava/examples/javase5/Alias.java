package ejava.examples.javase5;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This is an example annotation for giving something an alternate name.
 * 
 * @author jcstaff
 * $Id:$
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Alias {
    String value();
}
