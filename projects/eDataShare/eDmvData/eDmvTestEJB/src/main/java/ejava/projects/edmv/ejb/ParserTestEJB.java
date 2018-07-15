package ejava.projects.edmv.ejb;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.*;


import gov.ojp.it.jxdm._3_0.Person;
import gov.ojp.it.jxdm._3_0.ReferenceType;
import gov.ojp.it.jxdm._3_0.ResidenceType;
import gov.ojp.it.jxdm._3_0.VehicleRegistration;
import info.ejava.projects.edmv._1.Dmv;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Stateless;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.projects.edmv.xml.EDmvParser;

@Stateless
public class ParserTestEJB implements ParserTestRemote {
	private static final Logger log = LoggerFactory.getLogger(ParserTestEJB.class);
	
	@Resource(name="vals/xmlFile")
	private String xmlFile;
	
	@PostConstruct
	public void init() {
		log.debug("*** ParserTestEJB ***");
		log.debug("xmlFile=" + xmlFile);		
	}

	public void ingest() throws Exception {
		log.info("ingest");
		
		InputStream is = null;
		
		try {
			log.trace("getting input file:" + xmlFile);
			is = this.getClass().getResourceAsStream(xmlFile);
			if (is == null) {
				throw new Exception(xmlFile + " was not found");
			}
			
			log.trace("creating parser");
			EDmvParser parser = new EDmvParser(Dmv.class, is);
			
			log.trace("starting parse loop");
			List<Person> people = new ArrayList<Person>();
			Object object=null;
			do {
		        object = parser.getObject(
		                    "Person", "VehicleRegistration");
		        if (object instanceof Person) {
		        	if (((Person)object).getId().contains("1000")) {
		        		log.debug("here");
		        	}
		            check((Person)object, people);
		        }
		        else if (object instanceof VehicleRegistration) {
		            check((VehicleRegistration)object, people);
		        }
		        else if (object != null) {
		            fail("object of unknown type:" + object);
		        }
			} while (object != null);
		}
		finally {
			if (is != null) is.close();
		}
	}

	protected void check(Person p, List<Person> people) {
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
	
	protected void check(VehicleRegistration r, List<Person> people) {
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
