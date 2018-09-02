package ejava.projects.esales.xml;

import ejava.projects.esales.dto.Account;

import ejava.projects.esales.dto.Address;
import ejava.projects.esales.dto.ESales;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 * This provides a basic test of a constructed DMV DTO graph to be 
 * successfully marshalled and de-marshalled to/from an XML steam. 
 *
 */
public class ESalesParserTest {
    private static final Logger log = LoggerFactory.getLogger(ESalesParserTest.class);
    private Marshaller m;
    
    @Before
    public void setUp() throws Exception {
        JAXBContext jaxbc = JAXBContext.newInstance(ESales.class);
        m = jaxbc.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        
    }
    
    @Test
    public void testCalendar() throws Exception {
    	log.info("*** testCalendar ***");
        DatatypeFactory dataFactory = DatatypeFactory.newInstance();
        log.info("DataTypeFactory=" + dataFactory);
        XMLGregorianCalendar cal = dataFactory.newXMLGregorianCalendar();
        log.info("XMLGregorianCalendar=" + cal.getClass());
        cal.setMonth(GregorianCalendar.MARCH);
        String xml = cal.toXMLFormat();
        log.debug("cal=" + xml);
        dataFactory.newXMLGregorianCalendar(xml);
        
        cal.setTimezone(0);
        
        Calendar jCal = Calendar.getInstance();
        jCal.clear();
        jCal.set(Calendar.MONTH, Calendar.MARCH);
        DateFormat df = DateFormat.getDateInstance();
        String dfString = df.format(jCal.getTime()); 
        log.debug("calendar=" + dfString);

        String format = "--01";
        try {
	        XMLGregorianCalendar xCal = dataFactory.newXMLGregorianCalendar(format);
	        log.info("successfully parsed:" + format + ", xCal=" + xCal.toXMLFormat());
	        format = "--01--";
	        xCal = dataFactory.newXMLGregorianCalendar(format);
	        log.info("successfully parsed:" + format + ", xCal=" + xCal.toXMLFormat());
        }
        catch (Exception ex) {
        	log.error("failed to parse:" + format);
        	fail("failed to parse:" + format);
        }
    }

    @Test
    public void testMarshallDemarshall() throws Exception {
        log.info("*** testMarshallDemarshall ***");
        ESales sales = new SampleGen().createSales();
        
        File xmlFile = new File("target/test-classes/sales.xml");
        FileOutputStream fos = new FileOutputStream(xmlFile);
        m.marshal(sales, fos);
        fos.close();

        FileInputStream fis = new FileInputStream(xmlFile);
        ESalesParser parser = new ESalesParser(ESales.class, fis);
        Object object=null;
        while ((object = 
            parser.getObject(
                    "Address", "Account", "Auction", "Bid")) != null) {
            log.debug("{}",object);
            if (object instanceof Address) {
                Address address = (Address) object;
                Address expected = 
                    (Address)getById(
                            address.getId(), 
                            sales.getAddress());
                assertNotNull("unexpected Address id:" + address.getId(),
                        expected);
                compare(expected, address);
            }
            if (object instanceof Account) {
                Account account = (Account) object;
                Account expected = 
                    (Account)getByRefId(
                            account.getRefid(), 
                            sales.getAccount());
                assertNotNull("unexpected Account refId:" + account.getRefid(),
                        expected);
                compare(expected, account);
            }
        }        
    }
    
	private Object getById(int id, List<?> objects) {        
        Object theObject=null;
        for (Object object : objects) {
            Method getId;
            int theId;
            try {
                getId = object.getClass().getMethod("getId", new Class[]{});
                theId = (Integer)getId.invoke(object, new Object[]{});
                if (id == theId) {
                    theObject = object;
                    break;
                }
            } catch (Exception e) {
                fail(e.toString());
            }
        }
        return theObject;
    }
    
    private Object getByRefId(String id, List<?> objects) {        
        Object theObject=null;
        for (Object object : objects) {
            Method getId;
            String theId;
            try {
                getId = object.getClass().getMethod("getRefid", new Class[]{});
                theId = (String)getId.invoke(object, new Object[]{});
                if (id.equals(theId)) {
                    theObject = object;
                    break;
                }
            } catch (Exception e) {
                fail(e.toString());
            }
        }
        return theObject;
    }
    
    @SuppressWarnings("unused")
	private Account getByLogin(String login, List<Account> accounts) {
    	for (Account account : accounts) {
    		if (account.getLogin().equals(login)) {
    			return account;
    		}
    	}
    	return null;
    }
    
    protected boolean isEqual(String lhs, String rhs) {
        return ((lhs == rhs) ||
                 (lhs == null && rhs != null && rhs.length() == 0) ||
                 (rhs == null && lhs != null && lhs.length() == 0) ||
                 lhs.equals(rhs));
    }
    
    protected void compare(Address expected, Address actual) {
        log.info("checking address:" + expected.getId());
        assertFalse("actual was same object as expected", expected == actual);
        assertEquals("addressee", 
        		expected.getAddressee(), actual.getAddressee());
        assertEquals("city", 
        		expected.getCity(), actual.getCity());
        assertEquals("name", 
        		expected.getName(), actual.getName());
        assertEquals("state", 
        		expected.getState(), actual.getState());
        assertEquals("street", 
        		expected.getStreet(), actual.getStreet());
        assertEquals("zip", 
        		expected.getZip(), actual.getZip());
    }

    private void compare(Account expected, Account actual) {
		// TODO Auto-generated method stub
        log.info("checking account:" + expected.getRefid());
        assertFalse("actual was same object as expected", expected == actual);
        assertEquals("e-mail", 
    		expected.getEmail(), actual.getEmail());
        assertEquals("firstName", 
        		expected.getFirstName(), actual.getFirstName());
        assertEquals("lastName", 
        		expected.getLastName(), actual.getLastName());
        assertEquals("login", 
        		expected.getLogin(), actual.getLogin());
        assertEquals("middleName", 
        		expected.getMiddleName(), actual.getMiddleName());
        assertEquals("addresses", 
        		expected.getAddress().size(), actual.getAddress().size());
        /* need to strip out just date
        assertEquals("startDate", 
        		expected.getStartDate().getTime(), actual.getStartDate().getTime());
        		*/
	}
}
