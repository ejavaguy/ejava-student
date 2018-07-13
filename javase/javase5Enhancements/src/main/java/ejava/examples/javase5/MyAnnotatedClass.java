package ejava.examples.javase5;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Alias("demo class")
public class MyAnnotatedClass {
    private static final Logger logger = LoggerFactory.getLogger(MyAnnotatedClass.class);
    
    @CallMe(order=3, alias="last")
    public void one() { logger.info("one called"); }
    
    public void two() { logger.info("two called"); }
    
    @CallMe(order=0) 
    @Alias("first")
    public void three() { logger.info("three called"); }
    
    @CallMe(order=1, alias="middle")
    public void four() { logger.info("four called"); }
}
