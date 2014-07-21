package ejava.examples.javase5;

import static org.junit.Assert.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

public class AutoboxingTest {
    private static final Log log = LogFactory.getLog(AutoboxingTest.class);
    
    
    private Integer passInteger(Integer i) {
        log.info("received Integer=" + i);
        return i;
    }
    private Long passLong(Long i) {
        log.info("received Long=" + i);
        return i;
    }

    @Test
    public void testAutobox() {
        log.info("testAutoBox");
        
        //parameter values being manually wrapped
        //return values being manually unwrapped
        int intWrap = passInteger(new Integer(1)).intValue();
        long longWrap = passLong(new Long(1)).longValue();
        
        //parameter values being automatically wrapped ("auto-boxing")
        //return values being automatically unwrapped ("auto-unboxing")
        int intBox = passInteger(1);
        long longBox = passLong(1L);
        
        assertEquals(intWrap, intBox);
        assertEquals(longWrap, longBox);
    }
}
