package ejava.jpa.examples.tuning.suites;

import org.junit.runner.RunWith;


import org.junit.runners.Suite;

import ejava.jpa.examples.tuning.SuiteBase;
import ejava.jpa.examples.tuning.env.NonUniqueIndex;
import ejava.jpa.examples.tuning.env.UniqueIndex;


@RunWith(Suite.class)
@Suite.SuiteClasses({
	NonUniqueIndex.class,
	//-NonNullIndex.class,
	UniqueIndex.class,
	//-NonNullUniqueIndex.class
})
public class IndexTest extends SuiteBase {
	public static final int MAX_ROWS = 1000;
}
