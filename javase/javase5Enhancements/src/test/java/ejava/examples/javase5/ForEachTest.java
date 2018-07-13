package ejava.examples.javase5;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;

public class ForEachTest {
    private static final Logger logger = LoggerFactory.getLogger(ForEachTest.class); 
    
    /**
     * legacy way
     */
    @Test
    public void testIteratorCollection() {
        logger.info("testForEachCollection");
        
        Collection<String> collection = new ArrayList<String>();
        collection.add(new String("1"));
        collection.add(new String("2"));
        collection.add(new String("3"));
        
        int i=0;
        for(Iterator<String> itr = collection.iterator(); itr.hasNext(); ) {
            logger.info(itr.next());
            i++;
        }
        assertTrue("unexpected count:" + i, i==collection.size());        
    }

    /**
     * java SE 5 way
     *
     */
    @Test
    public void testForLoopCollection() {
        logger.info("testForEachCollection");
        
        Collection<String> collection = new ArrayList<String>();
        collection.add(new String("1"));
        collection.add(new String("2"));
        collection.add(new String("3"));
        
        int i=0;
        for(String s: collection) {
            logger.info(s);
            i++;
        }
        assertTrue("unexpected count:" + i, i==collection.size());        
    }

    /**
     * legacy way
     *
     */
    @Test
    public void testIteratorArray() {
        logger.info("testIteratorArray");
        
        String[] array = new String[3];
        array[0] = new String("1");
        array[1] = new String("2");
        array[2] = new String("3");
        
        int i=0;
        for(i=0; i<array.length; i++) {
            logger.info(array[i]);
        }
        assertTrue("unexpected count:" + i, i==array.length);        
    }
    
    /**
     * java SE 5 way
     *
     */
    @Test
    public void testForLoopArray() {
        logger.info("testForEachArray");
        
        String[] array = new String[3];
        array[0] = new String("1");
        array[1] = new String("2");
        array[2] = new String("3");
        
        int i=0;
        for(String s: array) {
            logger.info(s);
            i++;
        }
        assertTrue("unexpected count:" + i, i==array.length);        
    }
}
