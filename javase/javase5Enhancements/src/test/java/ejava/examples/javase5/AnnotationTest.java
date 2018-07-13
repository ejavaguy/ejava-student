package ejava.examples.javase5;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;

public class AnnotationTest {
    private static final Logger logger = LoggerFactory.getLogger(AnnotationTest.class);
    
    private void log(String title, Object[] values) {
        logger.info(title + " contained " + values.length + " elements");
        for (Object o : values) {
            logger.info("{}",o);
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
