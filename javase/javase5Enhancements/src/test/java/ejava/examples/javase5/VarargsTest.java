package ejava.examples.javase5;

import static org.junit.Assert.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;

public class VarargsTest {
    private static final Logger logger = LoggerFactory.getLogger(VarargsTest.class);
    
    private int sumArray(Integer[] values) {
        int result = 0;
        for (int i=0; i<values.length; i++) {
            result += values[i];
            logger.info("sum: result +=" + values[i] + "=" + result);
        }
        return result;
    }
    
    private int sumArgs(int...values) {
        int result = 0;
        for(int value : values) {
            result += value;
            logger.info("sum: result +=" + value + "=" + result);
        }
        return result;
        
    }
    
    @Test
    public void testVarargs() {
        int result1 = sumArray(new Integer[] { new Integer(1), new Integer(2), new Integer(3)});
        assertTrue("result1 unexpected value:" + result1, result1 == 6);

        int result2 = sumArgs(4, 5, 6);
        assertTrue("result2 unexpected value:" + result2, result2 == 15);
    }
}
