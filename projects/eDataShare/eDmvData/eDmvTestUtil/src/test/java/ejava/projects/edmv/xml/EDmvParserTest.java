package ejava.projects.edmv.xml;

import gov.ojp.it.jxdm._3_0.Person;

import gov.ojp.it.jxdm._3_0.ReferenceType;
import gov.ojp.it.jxdm._3_0.ResidenceType;
import gov.ojp.it.jxdm._3_0.VehicleRegistration;
import info.ejava.projects.edmv._1.Dmv;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.TestCase;

/**
 * This class provides a quick sanity check of the provided XML file. The
 * contents of the XML file will be obtained through reflection and printed
 * to stdout.
 * 
 * @author jcstaff
 *
 */
public class EDmvParserTest extends TestCase {
	private static final Log log = LogFactory.getLog(EDmvParserTest.class);
	private String inputDir = System.getProperty("inputDir");
	private List<Person> people = new ArrayList<Person>();
	
	public void setUp() {
		assertNotNull("inputDir not supplied", inputDir);
	}
	
	public void testMonthFormat() throws Exception {
		log.info("*** testMonthFormat ***");
		XMLGregorianCalendar cal1 = DatatypeFactory.newInstance().newXMLGregorianCalendar();
		cal1.setMonth(GregorianCalendar.MARCH);
		String xml = cal1.toXMLFormat();
		log.debug("MAR=" + xml);
		
		XMLGregorianCalendar cal = DatatypeFactory.newInstance().newXMLGregorianCalendar(xml);
		assertNotNull("calendar was null", cal);
		log.info("month=" + cal.getMonth());
		assertEquals("unexpected month", GregorianCalendar.MARCH, cal.getMonth());
	}
	
    public void testMonthParse() throws Exception {
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

	
	public void testParser() throws Exception {
		File inDir = new File(inputDir);
		File[] files = inDir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return (name.startsWith("dmv-") &&
						name.endsWith(".xml"));
			}
		});
		for (File file : files) {
			testParser(file.getCanonicalPath());
		}
	}
	public void testParser(String inputFile) throws Exception {
		log.info("*** testParser:" + inputFile + " ***");
		
		InputStream is = new FileInputStream(inputFile);
		BufferedInputStream bis = new BufferedInputStream(is);
		EDmvParser parser = new EDmvParser(Dmv.class, bis);
		Object object=null;
		do {
	        object = parser.getObject(
	                    "Person", "VehicleRegistration");
	        if (object instanceof Person) {
	        	if (((Person)object).getId().contains("1000")) {
	        		log.debug("here");
	        	}
	            check((Person)object);
	        }
	        else if (object instanceof VehicleRegistration) {
	            check((VehicleRegistration)object);
	        }
	        else if (object != null) {
	            fail("object of unknown type:" + object);
	        }
		} while (object != null);
		bis.close();
	}
	
	protected void check(Person p) {
	    log.info("checking person:" + p.getId());
	    
	    assertNotNull(p.getId());
        assertNotNull(p.getSourceIDText());
        assertNotNull(p.getPersonBirthDate());
        assertNotNull(p.getPersonBirthDate().getValue());
        assertNotNull(p.getPersonName());
        assertNotNull(p.getPersonName().getPersonGivenName());
        assertNotNull(p.getPersonName().getPersonGivenName().getValue());
        assertNotNull(p.getPersonName().getPersonMiddleName());
        assertNotNull(p.getPersonName().getPersonMiddleName().getValue());
        assertNotNull(p.getPersonName().getPersonSurName());
        assertNotNull(p.getPersonName().getPersonSurName().getValue());
        //not all names have a suffix
        assertNotNull(p.getPersonPhysicalDetails());
        //no photos are being supplied
        assertNotNull(p.getPersonPhysicalDetails().getPersonEyeColorCode().getValue());
        assertNotNull(p.getPersonPhysicalDetails().getPersonHairColorText().getValue());
        assertNotNull(p.getPersonPhysicalDetails().getPersonHeightMeasure().getValue());
        assertNotNull(p.getPersonPhysicalDetails().getPersonWeightMeasure().getValue());
        //race code is not being supplied
        assertNotNull(p.getPersonPhysicalDetails().getPersonSexCode().getValue());
        assertNotNull(p.getResidence());
        for (ResidenceType res : p.getResidence()) {
            assertNotNull(res.getResidenceStartDate());
            assertNotNull(res.getResidenceStartDate().getValue());
            //end date may not be supplied
            assertNotNull(res.getLocationAddress());
            assertNotNull(res.getLocationAddress().getLocationCityName());
            assertNotNull(res.getLocationAddress().getLocationCityName().getValue());
            assertNotNull(res.getLocationAddress().getLocationPostalCodeID());
            assertNotNull(res.getLocationAddress().getLocationPostalCodeID().getID());
            assertNotNull(res.getLocationAddress().getLocationPostalCodeID().getID().getValue());
            assertNotNull(res.getLocationAddress().getLocationStateCodeUSPostalService());
            assertNotNull(res.getLocationAddress().getLocationStateCodeUSPostalService().getValue());
            assertNotNull(res.getLocationAddress().getLocationStreet());
            assertNotNull(res.getLocationAddress().getLocationStreet().getStreetName());
            assertNotNull(res.getLocationAddress().getLocationStreet().getStreetName().getValue());
            assertNotNull(res.getLocationAddress().getLocationStreet().getStreetNumberText());
            assertNotNull(res.getLocationAddress().getLocationStreet().getStreetNumberText().getValue());
        }
        
        people.add(p);
	}
	
	protected void check(VehicleRegistration r) {
	    log.info("checking registration:" + r.getId());
	    
	    assertNotNull(r.getId());
	    assertNotNull(r.getSourceIDText());
	    assertNotNull(r.getVehicle());
	    assertNotNull(r.getVehicle().getPropertyOwnerPerson());
	    for (ReferenceType ref : r.getVehicle().getPropertyOwnerPerson()) {
	        assertNotNull(ref.getRef());
	        assertTrue(ref.getRef() instanceof Person);
	        Person o = (Person)ref.getRef();
	        assertTrue(people.contains(o));
	    }
	    assertNotNull(r.getVehicle().getVehicleColorPrimaryCode());
	    assertNotNull(r.getVehicle().getVehicleColorPrimaryCode().getValue());
	    assertNotNull(r.getVehicle().getVehicleID());
	    assertNotNull(r.getVehicle().getVehicleID().getID());
	    assertNotNull(r.getVehicle().getVehicleMakeCode());
	    assertNotNull(r.getVehicle().getVehicleMakeCode().getValue());
	    assertNotNull(r.getVehicle().getVehicleModelCode());
	    assertNotNull(r.getVehicle().getVehicleModelCode().getValue());
	    assertNotNull(r.getVehicle().getVehicleModelYearDate());
	    assertNotNull(r.getVehicle().getVehicleModelYearDate().getValue());
	    assertNotNull(r.getVehicleLicensePlateID());
	    assertNotNull(r.getVehicleLicensePlateID().getID());
	    assertNotNull(r.getVehicleLicensePlateID().getID().getValue());
	    assertNotNull(r.getVehicleRegistrationDecal());
	    assertNotNull(r.getVehicleRegistrationDecal().getDecalMonthDate());
	    //try {
	    assertNotNull("decal month date is null",
	    		r.getVehicleRegistrationDecal().getDecalMonthDate().getValue());
	    //} catch (Throwable ex) {
	    //	log.error("monthDate.getValue() == null");
	    //}
	    assertNotNull(r.getVehicleRegistrationDecal().getDecalYearDate());
	    assertNotNull(r.getVehicleRegistrationDecal().getDecalYearDate().getValue());
	}

}
