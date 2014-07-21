package ejava.examples.javase5;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.TestCase;

public class EnumTest extends TestCase {
    private static final Log log = LogFactory.getLog(EnumTest.class);
    
    private enum Day { SUN, MON, TUE, WED, THU, FRI, SAT };
    
    public void testEnum() {
        
        Day day1 = Day.SUN;
        Day day2 = Day.WED;
        log.debug("day1=" + day1.name() + ", ordinal=" + day1.ordinal());
        log.debug("day2=" + day2.name() + ", ordinal=" + day2.ordinal());
        
        assertTrue(day1 + " wasn't before " + day2, day1.compareTo(day2) < 0);        
    }
    
    
    private enum Rate {SUN(2), MON(1), TUE(1), WED(1), THU(1), FRI(1), SAT(1.5);
       private double amount; 
       private Rate(double amount) { this.amount = amount; }
       public double amount() { return amount; }
    }
    
    public void testEnumValues() {
        int hours = 4;
        Double wage = 10.0;
        
        for (Rate rate : Rate.values()) {
            Double pay = hours * wage * rate.amount();
            log.info("pay for " + rate.name() + "=" + pay);    
        }                
    }    
    
    private enum Worker { HARD { public String go() { return "CAN DO!"; } },
                          GOOD { public String go() { return "I'll try"; } },
                          BAD  { public String go() { return "Why?"; } };
        public abstract String go();
    }

    public void testEnumBehavior() {
        for (Worker w : Worker.values()) {
            log.info(w.name() + " worker says \'" + w.go() + "\' when tasked");
        }
    }
}
