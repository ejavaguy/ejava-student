package ejava.projects.esales.blimpl;

import java.io.InputStream;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.projects.esales.dao.AccountDAO;
import ejava.projects.esales.dto.ESales;
import ejava.projects.esales.xml.ESalesParser;

public class ESalesIngestor {
	private static final Logger log = LoggerFactory.getLogger(ESalesIngestor.class);
	InputStream is;
	AccountDAO accountDAO;
	ESalesParser parser;
	
	public void setInputStream(InputStream is) {
		this.is = is; 
	}
	
	public void setAccountDAO(AccountDAO accountDAO) {
		this.accountDAO = accountDAO;
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
		ESalesParser parser = new ESalesParser(ESales.class, is);
		
		Object object = parser.getObject(
				"address", "account", "auction", "image");
		while (object != null) {
			if (object instanceof ejava.projects.esales.dto.Account) {
				createAccount((ejava.projects.esales.dto.Account)object);
			}
			object = parser.getObject(
					"address", "account", "auction", "image");
		}
	}
	
	/**
	 * This method is called by the main ingest processing loop. The JAXB/StAX
	 * parser will already have the Account populated with Address information.
	 * @param accountDTO
	 */
	private void createAccount(ejava.projects.esales.dto.Account accountDTO) {
		ejava.projects.esales.bo.Account accountBO = 
			new ejava.projects.esales.bo.Account(accountDTO.getLogin());
		accountBO.setFirstName(accountDTO.getFirstName());
		for (Object o : accountDTO.getAddress()) {
			ejava.projects.esales.dto.Address addressDTO = 
				(ejava.projects.esales.dto.Address)o;
			ejava.projects.esales.bo.Address addressBO = 
				new ejava.projects.esales.bo.Address(
						0,
						addressDTO.getName(),
						addressDTO.getCity());
			accountBO.getAddresses().add(addressBO);
		}
		accountDAO.createAccount(accountBO);
		log.debug("created account:" + accountBO);
	}
}
