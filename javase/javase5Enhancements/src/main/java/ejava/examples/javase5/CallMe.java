package ejava.examples.javase5;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * This is a test interface that will be used to designate which methods
 * of a class should get called, and in what order, within a demo. An
 * optional name is supplied with each method.
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CallMe {        
        int order();
        String alias() default "";        
}
