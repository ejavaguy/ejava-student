package ejava.examples.orm.ejbql;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * This class provides the main entry point for each of the individual test
 * cases. It provides one-time setUp and tearDown functionality needed by 
 * all individual test cases.
 * 
 * @author jcstaff
 * $Id:$
 */
@RunWith(Suite.class)
@SuiteClasses({
    QueryDemo.class,
    EJBQLDemo.class,
    BulkDemo.class,
    NativeQueryDemo.class,
    NamedQueryDemo.class
})
public class AllTest {
}
