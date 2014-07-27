package ejava.jpa.examples.tuning.suites;

import org.junit.runner.RunWith;

import org.junit.runners.Suite;

import ejava.jpa.examples.tuning.SuiteBase;
import ejava.jpa.examples.tuning.env.JoinCompositeFKIndexes;
import ejava.jpa.examples.tuning.env.JoinNoIndex;
import ejava.jpa.examples.tuning.env.JoinTableFKIndexes;
import ejava.jpa.examples.tuning.env.JoinTableIndexes;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	JoinNoIndex.class,
	JoinTableIndexes.class,
	JoinTableFKIndexes.class,
	JoinCompositeFKIndexes.class
})
public class JoinTest extends SuiteBase {
}
