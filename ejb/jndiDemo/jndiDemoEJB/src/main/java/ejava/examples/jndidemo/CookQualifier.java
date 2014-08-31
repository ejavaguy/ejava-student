package ejava.examples.jndidemo;

import javax.enterprise.util.AnnotationLiteral;

/**
 * This class is used to select CookEJB instances from an Injected
 * CDI bean Instance<T> provider.
 */
@SuppressWarnings("serial")
public class CookQualifier extends AnnotationLiteral<Cook>  {
}
