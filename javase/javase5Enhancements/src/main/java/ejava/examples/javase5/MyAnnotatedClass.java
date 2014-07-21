package ejava.examples.javase5;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Alias("demo class")
public class MyAnnotatedClass {
    private static final Log log = LogFactory.getLog(MyAnnotatedClass.class);
    
    @CallMe(order=3, alias="last")
    public void one() { log.info("one called"); }
    
    public void two() { log.info("two called"); }
    
    @CallMe(order=0) 
    @Alias("first")
    public void three() { log.info("three called"); }
    
    @CallMe(order=1, alias="middle")
    public void four() { log.info("four called"); }
}
