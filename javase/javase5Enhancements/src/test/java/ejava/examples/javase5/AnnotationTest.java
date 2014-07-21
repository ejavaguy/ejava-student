package ejava.examples.javase5;

import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

public class AnnotationTest {
    private static final Log log = LogFactory.getLog(AnnotationTest.class);
    
    private void log(String title, Object[] values) {
        log.info(title + " contained " + values.length + " elements");
        for (Object o : values) {
            log.info(o);
        }
    }
    
    private void invoke(Object obj) throws Exception {
        Class<? extends Object> clazz = obj.getClass();
        log("class annotations", clazz.getAnnotations());

        for(Method m : clazz.getDeclaredMethods()) {
            log(m.getName() + " annotations", m.getAnnotations());
        }
    }
    
    @Test
    public void testAnnotations() throws Exception {
        MyAnnotatedClass myObject = new MyAnnotatedClass();
        invoke(myObject);
    }        

}
