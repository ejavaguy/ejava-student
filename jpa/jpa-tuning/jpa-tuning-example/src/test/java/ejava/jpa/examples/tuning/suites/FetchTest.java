package ejava.jpa.examples.tuning.suites;

import org.junit.runner.RunWith;


import org.junit.runners.Suite;

import ejava.jpa.examples.tuning.SuiteBase;
import ejava.jpa.examples.tuning.env.FetchEagerThick;
import ejava.jpa.examples.tuning.env.FetchEagerThin;
import ejava.jpa.examples.tuning.env.FetchLazyThin;
import ejava.jpa.examples.tuning.env.FetchLazyThick;

/**
 * This test suite evaluates the use of different uses of fetch=LAZY and
 * fetch=EAGER
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
	FetchLazyThin.class,
	FetchEagerThin.class,
	FetchEagerThick.class,
	FetchLazyThick.class
})
public class FetchTest extends SuiteBase {
}
