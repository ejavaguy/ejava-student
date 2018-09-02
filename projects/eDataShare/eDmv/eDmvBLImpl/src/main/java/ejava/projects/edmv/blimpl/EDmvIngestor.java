package ejava.projects.edmv.blimpl;

import info.ejava.projects.edmv._1.Dmv;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.projects.edmv.bl.EDmvException;
import ejava.projects.edmv.dao.DAOException;
import ejava.projects.edmv.dao.PersonDAO;
import ejava.projects.edmv.dao.VehicleDAO;
import ejava.projects.edmv.xml.EDmvParser;

/**
 * This class provides a _sparse_ example implementation of how one can use
 * the parser to ingest the information from the XML file to populate the
 * database.
 */
public class EDmvIngestor {
	private static final Logger log = LoggerFactory.getLogger(EDmvIngestor.class);
	InputStream is;
	PersonDAO personDAO;
	VehicleDAO vehicleDAO;
	EDmvParser parser;
	Map<String, ejava.projects.edmv.bo.Person> dto2bo = 
	    new HashMap<String, ejava.projects.edmv.bo.Person>();
	
	public void setInputStream(InputStream is) {
		this.is = is; 
	}
	
	public void setPersonDAO(PersonDAO personDAO) {
		this.personDAO = personDAO;
	}
	
    public void setVehicleDAO(VehicleDAO vehicleDAO) {
        this.vehicleDAO = vehicleDAO;
    }

    /**
	 * This method will ingest the input data by reading in external DTOs in
	 * from the parser, instantiating project business objects, and inserting
	 * into database. Note that the XML Schema is organized such that object
	 * references are fully resolved. Therefore, there is no specific need
	 * to process the addresses as they come in. They can be stored once we
	 * get the accounts they are related to.
	 * 
	 * @throws JAXBException
	 * @throws XMLStreamException
     * @throws DAOException 
	 */
	public void ingest() 
	    throws JAXBException, XMLStreamException, Exception {
		EDmvParser parser = new EDmvParser(Dmv.class, is);
		
		Object object = parser.getObject(
		        "Person", "VehicleRegistration");
		while (object != null) {
			if (object instanceof gov.ojp.it.jxdm._3_0.Person) {
				createPerson((gov.ojp.it.jxdm._3_0.Person)object);
			}
            if (object instanceof gov.ojp.it.jxdm._3_0.VehicleRegistration) {
                createRegistration((gov.ojp.it.jxdm._3_0.VehicleRegistration)object);
            }
			object = parser.getObject(
			        "Person", "VehicleRegistration");
		}
	}
	
	/**
	 * This method is called by the main ingest processing loop. The JAXB/StAX
	 * parser will already have the Person populated with internal property
	 * information.
	 * @param personDTO
	 * @throws DAOException 
	 */
	private void createPerson(gov.ojp.it.jxdm._3_0.Person personDTO) 
	    throws DAOException {
	    ejava.projects.edmv.bo.Person personBO =
	        new ejava.projects.edmv.bo.Person();
	    personBO.setLastName(
	            personDTO.getPersonName().getPersonSurName().getValue());
    		
   		personDAO.createPerson(personBO);
   		log.debug("created person:" + personBO);
   		
   		//map DTO id to BO object for follow-on ownership processing
   		dto2bo.put(personDTO.getId(), personBO);
	}

    /**
     * This method is called by the main ingest processing loop. The JAXB/StAX
     * parser will already have the VehicleRegistration populated with internal 
     * property information. The Person information for owners should also
     * already exist.
     * 
     * @param registrationDTO
     * @throws DAOException 
     */
    private void createRegistration(
            gov.ojp.it.jxdm._3_0.VehicleRegistration registrationDTO) 
        throws Exception {
        ejava.projects.edmv.bo.VehicleRegistration registrationBO =
            new ejava.projects.edmv.bo.VehicleRegistration();
        registrationBO.setVin(
                registrationDTO.getVehicle().getVehicleID().getID().getValue());
        
        //add the m-m owners
        for (gov.ojp.it.jxdm._3_0.ReferenceType ref: 
                registrationDTO.getVehicle().getPropertyOwnerPerson()) {
            gov.ojp.it.jxdm._3_0.Person ownerDTO =
                (gov.ojp.it.jxdm._3_0.Person)ref.getRef();
            ejava.projects.edmv.bo.Person ownerBO = 
                dto2bo.get(ownerDTO.getId());
            if (ownerBO == null) {
                throw new EDmvException("owner not found:" + ownerDTO.getId()); 
            }
            
            registrationBO.getOwners().add(ownerBO);
        }
            
        vehicleDAO.createRegistration(registrationBO);
        log.debug("created registration:" + registrationBO);
    }
}
