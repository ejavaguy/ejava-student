package ejava.jpa.examples.tuning.suites;

import org.junit.runner.RunWith;

import org.junit.runners.Suite;
import ejava.jpa.examples.tuning.SuiteBase;
import ejava.jpa.examples.tuning.env.CompositeSelectWhere;
import ejava.jpa.examples.tuning.env.CompositeWhereSelect;
import ejava.jpa.examples.tuning.env.NoColumnIndex;
import ejava.jpa.examples.tuning.env.WhereColumnIndex;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	NoColumnIndex.class,
	WhereColumnIndex.class,
	CompositeWhereSelect.class,
	CompositeSelectWhere.class
})
public class ValueQueryTest extends SuiteBase {
}
