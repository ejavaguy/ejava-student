package ejava.jpa.examples.tuning.suites;

import org.junit.runner.RunWith;

import org.junit.runners.Suite;

import ejava.jpa.examples.tuning.SuiteBase;
import ejava.jpa.examples.tuning.env.CompositeIndex;
import ejava.jpa.examples.tuning.env.DualIndexes;

/**
 * This test suite evaluates the use of a composite index.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
	DualIndexes.class,
	CompositeIndex.class
})
public class CompositeIndexTest extends SuiteBase {
}
