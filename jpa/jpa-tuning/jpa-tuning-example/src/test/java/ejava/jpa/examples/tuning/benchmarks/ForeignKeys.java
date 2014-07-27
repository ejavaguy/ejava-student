package ejava.jpa.examples.tuning.benchmarks;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Test;

import com.carrotsearch.junitbenchmarks.annotation.AxisRange;

import ejava.jpa.examples.tuning.TestBase;
import ejava.jpa.examples.tuning.TestLabel;
import ejava.jpa.examples.tuning.bo.Person;
import ejava.jpa.examples.tuning.suites.ForeignKeyIndexTest;

@AxisRange(min=ForeignKeyIndexTest.AXIS_MIN, max=ForeignKeyIndexTest.AXIS_MAX)
public class ForeignKeys extends TestBase {
	protected static Person kevinBacon;
	protected static Date TREMORS_RELEASE_DATE=new GregorianCalendar(1990, Calendar.JULY, 1).getTime();

	
	@TestLabel(label="Get Children")
	@Test
	public void getChildren() {
		getDAO().getRolesByMovie("Tremors", TREMORS_RELEASE_DATE, null, null, null);
	}
	
	@TestLabel(label = "Single JOIN")
	//@Test
	public void getKevin() {
		getDAO().getKevinBacon();
	}

	@TestLabel(label = "Multiple JOINs")
	//@Test
	public void kevin1Step() {
		getDAO().oneStepFromPersonByDB(kevinBacon, null, null, null);
	}

}