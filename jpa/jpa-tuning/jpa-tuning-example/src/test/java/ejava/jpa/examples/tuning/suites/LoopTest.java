package ejava.jpa.examples.tuning.suites;

import org.junit.runner.RunWith;


import org.junit.runners.Suite;
import ejava.jpa.examples.tuning.SuiteBase;
import ejava.jpa.examples.tuning.env.LoopEager;
import ejava.jpa.examples.tuning.env.LoopLazy;

/**
 * This test suite evaluates the use of loops within queries.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
	LoopLazy.class,
	LoopEager.class
})
public class LoopTest extends SuiteBase {
}
