package ejava.projects.edmv.xml;

import gov.ojp.it.jxdm._3_0.Person;
import gov.ojp.it.jxdm._3_0.PersonPhysicalDetailsType;
import gov.ojp.it.jxdm._3_0.ResidenceType;
import gov.ojp.it.jxdm._3_0.VehicleRegistration;
import info.ejava.projects.edmv._1.Dmv;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.TestCase;

/**
 * This provides a basic test of a constructed DMV DTO graph to be 
 * successfully marshalled and de-marshalled to/from an XML steam. 
 *
 */
public class EDmvBindingTest extends TestCase {
    private Logger log = LoggerFactory.getLogger(EDmvBindingTest.class);
    private Marshaller m;
    
    public void setUp() throws Exception {
        JAXBContext jaxbc = JAXBContext.newInstance(Dmv.class);
        m = jaxbc.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        
    }
    
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

    public void testMarshallDemarshall() throws Exception {
        log.info("*** testMarshallDemarshall ***");
        Dmv dmv = new SampleGen().createDmv();
        
        File xmlFile = new File("target/test-classes/dmv.xml");
        FileOutputStream fos = new FileOutputStream(xmlFile);
        m.marshal(dmv, fos);
        fos.close();

        FileInputStream fis = new FileInputStream(xmlFile);
        EDmvParser parser = new EDmvParser(Dmv.class, fis);
        Object object=null;
        while ((object = 
            parser.getObject(
                    "Person", "VehicleRegistration")) != null) {
                    //"VehicleRegistration")) != null) {
            log.debug("{}",object);
            if (object instanceof Person) {
                Person person = (Person) object;
                Person expected = 
                    (Person)getById(
                            person.getId(), 
                            dmv.getPeople().getPerson());
                assertNotNull("unexpected Person id:" + person.getId(),
                        expected);
                dump(person);
                compare(expected, person);
            }
            else if (object instanceof VehicleRegistration) {
                VehicleRegistration vreg = (VehicleRegistration)object;
                VehicleRegistration expected = 
                    (VehicleRegistration)getById(
                            vreg.getId(), 
                            dmv.getVehicleRegistrations().getVehicleRegistration());
                assertNotNull("unexpected registration id", vreg);
                dump(vreg);                
                compare(expected, vreg);
            }
        }        
    }
    
    private Object getById(String id, 
    		@SuppressWarnings("rawtypes") List objects) {        
        Object theObject=null;
        for (Object object : objects) {
            Method getId;
            String theId;
            try {
                getId = object.getClass().getMethod("getId", new Class[]{});
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
    
	private Object getBySourceIDText(String id, 
			@SuppressWarnings("rawtypes")List objects) {        
        Object theObject=null;
        for (Object object : objects) {
            Method getId;
            String theId;
            try {
                getId = object.getClass().getMethod("getSourceIDText", new Class[]{});
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
    
    protected boolean isEqual(String lhs, String rhs) {
        return ((lhs == rhs) ||
                 (lhs == null && rhs != null && rhs.length() == 0) ||
                 (rhs == null && lhs != null && lhs.length() == 0) ||
                 lhs.equals(rhs));
    }
    
    protected void compare(Person expected, Person actual) {
        log.info("checking person:" + expected.getId());
        assertFalse("actual was same object as expected", expected == actual);
        assertEquals("unexpected sourceIDText", 
                expected.getSourceIDText(), 
                actual.getSourceIDText()); 
        assertTrue("unexpected first name", isEqual(
                expected.getPersonName().getPersonGivenName().getValue(),
                actual.getPersonName().getPersonGivenName().getValue()));
        assertTrue("unexpected middle name", isEqual(
                expected.getPersonName().getPersonMiddleName().getValue(),
                actual.getPersonName().getPersonMiddleName().getValue()));
        assertTrue("unexpected last name", isEqual(
                expected.getPersonName().getPersonSurName().getValue(),
                actual.getPersonName().getPersonSurName().getValue()));
        assertTrue("unexpected suffix name", isEqual(
                expected.getPersonName().getPersonSuffixName().getValue(),
                actual.getPersonName().getPersonSuffixName().getValue()));
        assertEquals("unexpected birth date", 0,
                expected.getPersonBirthDate().getValue().compare(
                actual.getPersonBirthDate().getValue()));
        compare(expected.getPersonPhysicalDetails(), 
                actual.getPersonPhysicalDetails());
        compare(expected.getResidence(), actual.getResidence());
    }
    
    protected void compare(
            PersonPhysicalDetailsType expected, PersonPhysicalDetailsType actual) {
        assertNotNull("no physical details", actual);
        assertTrue("unexpected eyes", isEqual(
                expected.getPersonEyeColorCode().getValue(),
                actual.getPersonEyeColorCode().getValue()));
        assertTrue("unexpected hair", isEqual(
                expected.getPersonHairColorText().getValue(),
                actual.getPersonHairColorText().getValue()));
        assertEquals("unexpected height", 
                expected.getPersonHeightMeasure().getValue(),
                actual.getPersonHeightMeasure().getValue());
        assertEquals("unexpected weight", 
                expected.getPersonWeightMeasure().getValue(),
                actual.getPersonWeightMeasure().getValue());
        assertEquals("unexpected sex", 
                expected.getPersonSexCode().getValue(),
                actual.getPersonSexCode().getValue());
        assertTrue("unexpected image", Arrays.equals(
                expected.getPersonDigitalImage().getBinaryObjectBase64().getValue(), 
                actual.getPersonDigitalImage().getBinaryObjectBase64().getValue()));
    }
    
    protected void compare(
            List<ResidenceType> expected, List<ResidenceType> actual) {
        for (ResidenceType exp : expected) {
            ResidenceType act = 
                (ResidenceType)getBySourceIDText(exp.getSourceIDText(), actual);
            assertNotNull("unexpected residence id:" + exp.getSourceIDText(),
                    act);
            assertEquals("unexpected start", 0,
                    exp.getResidenceStartDate().getValue().compare(
                            act.getResidenceStartDate().getValue()));
            assertEquals("unexpected start", 0,
                    exp.getResidenceStartDate().getValue().compare(
                            act.getResidenceStartDate().getValue()));
            if (exp.getResidenceEndDate() != null &&
                    exp.getResidenceEndDate().getValue() != null) {
                assertEquals("unexpected end", 0,
                        exp.getResidenceEndDate().getValue().compare(
                                act.getResidenceEndDate().getValue()));
            }
            assertEquals("unexpected street number",
                    exp.getLocationAddress().getLocationStreet().getStreetNumberText().getValue(),
                    act.getLocationAddress().getLocationStreet().getStreetNumberText().getValue());
            assertEquals("unexpected street name",
                    exp.getLocationAddress().getLocationStreet().getStreetName().getValue(),
                    act.getLocationAddress().getLocationStreet().getStreetName().getValue());
            assertEquals("unexpected city",
                    exp.getLocationAddress().getLocationCityName().getValue(),
                    act.getLocationAddress().getLocationCityName().getValue());
            assertEquals("unexpected street state",
                    exp.getLocationAddress().getLocationStateCodeUSPostalService().getValue(),
                    act.getLocationAddress().getLocationStateCodeUSPostalService().getValue());
            assertEquals("unexpected street zip",
                    exp.getLocationAddress().getLocationPostalCodeID().getID().getValue(),
                    act.getLocationAddress().getLocationPostalCodeID().getID().getValue());
        }
    }
    
    protected void dump(Person person) {
        StringBuilder text = new StringBuilder();
        text.append("id=" + person.getId());
        text.append("name=" + 
                person.getPersonName().getPersonGivenName().getValue());
        text.append(" " + 
                person.getPersonName().getPersonMiddleName().getValue());
        text.append(" " + 
                person.getPersonName().getPersonSurName().getValue());
        text.append(" " + 
                person.getPersonName().getPersonSuffixName().getValue());
        
        log.debug("person=" + text);
    }
    
    protected void compare(
            VehicleRegistration expected, VehicleRegistration actual) {
        log.info("checking registration:" + expected.getId());
        assertEquals("unexpected tagId",
                expected.getVehicleLicensePlateID().getID().getValue(),
                actual.getVehicleLicensePlateID().getID().getValue());
        assertEquals("unexpected tag month",
                expected.getVehicleRegistrationDecal().getDecalMonthDate().getValue().getMonth(),
                actual.getVehicleRegistrationDecal().getDecalMonthDate().getValue().getMonth());
        assertEquals("unexpected tag year",
                expected.getVehicleRegistrationDecal().getDecalYearDate().getValue().getYear(),
                actual.getVehicleRegistrationDecal().getDecalYearDate().getValue().getYear());
        
        assertEquals("unexpected color",
                expected.getVehicle().getVehicleColorPrimaryCode().getValue(),
                actual.getVehicle().getVehicleColorPrimaryCode().getValue());
        assertEquals("unexpected vin",
                expected.getVehicle().getVehicleID().getID().getValue(),
                actual.getVehicle().getVehicleID().getID().getValue());
        assertEquals("unexpected make",
                expected.getVehicle().getVehicleMakeCode().getValue(),
                actual.getVehicle().getVehicleMakeCode().getValue());
        assertEquals("unexpected model",
                expected.getVehicle().getVehicleModelCode().getValue(),
                actual.getVehicle().getVehicleModelCode().getValue());
        assertEquals("unexpected model",
                expected.getVehicle().getVehicleModelYearDate().getValue().getYear(),
                actual.getVehicle().getVehicleModelYearDate().getValue().getYear());
        
        assertEquals("unexpected owner count",
                expected.getVehicle().getPropertyOwnerPerson().size(),
                actual.getVehicle().getPropertyOwnerPerson().size());
        for (int i=0; i<expected.getVehicle().getPropertyOwnerPerson().size(); i++) {
        	assertNotNull("expected vehicle owner was null",
        			expected.getVehicle().getPropertyOwnerPerson().get(0));
        	assertNotNull("actual vehicle owner was null",
        			actual.getVehicle().getPropertyOwnerPerson().get(0));
                log.debug("expected size()=" + 
                    expected.getVehicle().getPropertyOwnerPerson().size());
                log.debug("expected get(0)=" + 
                    expected.getVehicle().getPropertyOwnerPerson().get(0));
                log.debug("actual size()=" + 
                    actual.getVehicle().getPropertyOwnerPerson().size());
                log.debug("actual get(0)=" + 
                    actual.getVehicle().getPropertyOwnerPerson().get(0));
                log.debug("actual get(0)=" + 
                    actual.getVehicle().getPropertyOwnerPerson().get(0).getClass());

        	assertNotNull("expected vehicle owner ref was null",
        			expected.getVehicle().getPropertyOwnerPerson().get(0).getRef());
        	assertNotNull("actual vehicle owner ref was null",
        			actual.getVehicle().getPropertyOwnerPerson().get(0).getRef());
            assertEquals("unexpected owner",
                    ((Person)expected.getVehicle().getPropertyOwnerPerson().get(0).getRef()).getId(),
                    ((Person)actual.getVehicle().getPropertyOwnerPerson().get(0).getRef()).getId()
                    );
        }
    }
    
    protected void dump(VehicleRegistration vreg) {
        StringBuilder text = new StringBuilder();
        text.append("id=" + vreg.getId());
        
        log.debug("vreg=" + text);
        
	    assertNotNull(vreg.getVehicleRegistrationDecal());
	    assertNotNull(vreg.getVehicleRegistrationDecal().getDecalMonthDate());
        assertNotNull(vreg.getVehicleRegistrationDecal().getDecalMonthDate().getValue());
	    assertNotNull(vreg.getVehicleRegistrationDecal().getDecalYearDate());
	    assertNotNull(vreg.getVehicleRegistrationDecal().getDecalYearDate().getValue());
	    
	    log.debug("month=" + vreg.getVehicleRegistrationDecal().getDecalMonthDate().getValue());
	    log.debug("year=" + vreg.getVehicleRegistrationDecal().getDecalYearDate().getValue());

    }
}
