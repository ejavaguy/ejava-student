package ejava.projects.eleague.blimpl;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.projects.eleague.dao.ClubDAO;
import ejava.projects.eleague.dto.ELeague;
import ejava.projects.eleague.dto.Season;
import ejava.projects.eleague.xml.ELeagueParser;

public class LeagueIngestor {
	private static final Logger logger = LoggerFactory.getLogger(LeagueIngestor.class);
	private InputStream is;
	private ClubDAO clubDAO;
	
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
	 * @throws IOException 
	 */
	public void ingest() throws JAXBException, XMLStreamException, IOException {
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
		is.close();
	}
	
	private void checkSeason(Season season) {
		if ("Spring NeverEnds".equals(season.getName())) {
			logger.info("checking {} for null contact", season.getName());
			for (ejava.projects.eleague.dto.Division division : season.getDivision()) {
			    if (division.getContact() == null) {
			    	logger.error("current season has no contact, " +
			    			"check project version: refId {}", division.getRefid());
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
    		logger.debug("created venue: {} for club {}", venueBO, clubDTO.getName());
	    }
	}
}
