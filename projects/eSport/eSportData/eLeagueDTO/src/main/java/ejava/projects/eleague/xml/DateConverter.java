package ejava.projects.eleague.xml;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.bind.DatatypeConverter;

/**
 * This class will convert the dateTime strings from XML documents to/from
 * java.util.Date instead of the default XMLGregorianCalendar. 
 */
public class DateConverter {
    public static Date parseDate(String s) {
	    return DatatypeConverter.parseDate(s).getTime();
    }
	public static String printDate(Date dt) {
	    Calendar cal = new GregorianCalendar();
		cal.setTime(dt);
		return DatatypeConverter.printDateTime(cal);
    }
}
