package ejava.projects.eleague.blimpl;

import java.io.InputStream;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.projects.eleague.dao.ClubDAO;
import ejava.projects.eleague.dto.ELeague;
import ejava.projects.eleague.dto.Season;
import ejava.projects.eleague.xml.ELeagueParser;

public class ELeagueIngestor {
	private static final Log log = LogFactory.getLog(ELeagueIngestor.class);
	InputStream is;
	ClubDAO clubDAO;
	ELeagueParser parser;
	
	public void setInputStream(InputStream is) {
		this.is = is; 
	}
	
	public void setClubDAO(ClubDAO clubDAO) {
		this.clubDAO = clubDAO;
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
	 */
	public void ingest() throws JAXBException, XMLStreamException {
		ELeagueParser parser = new ELeagueParser(ELeague.class, is);
		
		Object object = parser.getObject(
				"contact", "league-metadata", "club", "season");
		while (object != null) {
			if (object instanceof ejava.projects.eleague.dto.Club) {
				createVenue((ejava.projects.eleague.dto.Club)object);
			}
			else if (object instanceof ejava.projects.eleague.dto.Season) {
				checkSeason((ejava.projects.eleague.dto.Season)object);
			}
			object = parser.getObject(
			        "contact", "league-metadata", "club", "season");
		}
	}
	
	private void checkSeason(Season season) {
		if ("Spring NeverEnds".equals(season.getName())) {
			log.info("checking " + season.getName() + " for null contact");
			for (ejava.projects.eleague.dto.Division division : season.getDivision()) {
			    if (division.getContact() == null) {
			    	log.error("current season has no contact, " +
			    			"check project version: refId" + division.getRefid());
			    }
			}
		}		
	}

	/**
	 * This method is called by the main ingest processing loop. The JAXB/StAX
	 * parser will already have the Venue populated with Address information.
	 * @param clubDTO
	 */
	private void createVenue(ejava.projects.eleague.dto.Club clubDTO) {
	    for (ejava.projects.eleague.dto.Venue venueDTO : clubDTO.getVenue()) {
    		ejava.projects.eleague.bo.Address addressBO = 
    			new ejava.projects.eleague.bo.Address();
    		addressBO.setCity(venueDTO.getCity());
    		
    		ejava.projects.eleague.bo.Venue venueBO =
    		    new ejava.projects.eleague.bo.Venue();
    		venueBO.setName(venueDTO.getName());
    		venueBO.setAddress(addressBO);
    		
    		clubDAO.createVenue(venueBO);
    		log.debug("created venue:" + venueBO + 
    		        " for club " + clubDTO.getName());
	    }
	}
}
