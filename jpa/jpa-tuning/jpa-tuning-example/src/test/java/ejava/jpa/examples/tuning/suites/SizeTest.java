package ejava.jpa.examples.tuning.suites;

import org.junit.runner.RunWith;

import org.junit.runners.Suite;

import ejava.jpa.examples.tuning.SuiteBase;
import ejava.jpa.examples.tuning.env.SizeEager;
import ejava.jpa.examples.tuning.env.SizeLazy;

/**
 * This test suite evaluates the use of getting the counnt of a collection
 * in various ways.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
	SizeLazy.class,
	SizeEager.class
})
public class SizeTest extends SuiteBase {
}
