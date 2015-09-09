package ejava.projects.esales.datagen;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.projects.esales.dao.ESalesDAO;
import ejava.projects.esales.dao.JPAESalesDAO;
import ejava.projects.esales.dto.Account;
import ejava.projects.esales.dto.Address;
import ejava.projects.esales.dto.Auction;
import ejava.projects.esales.dto.Bid;
import ejava.projects.esales.dto.ESales;
import ejava.projects.esales.dto.Image;

public class DataGenerator {
	Log log = LogFactory.getLog(DataGenerator.class);
	private ESalesDAO dao;
	public static final String OUTPUT_FILE = 
		"ejava.projects.esales.datagen.outputFile";
	private Marshaller m;
	private int refid=0;
	
	public DataGenerator() throws JAXBException {
		JAXBContext jaxbc = JAXBContext.newInstance(ESales.class);
		m = jaxbc.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	}
	
	public void setESalesDAO(ESalesDAO dao) {
		this.dao = dao;
	}
	
	public int generate(Writer writer, int auctionCount) 
	    throws JAXBException {
		ESales root = new ESales();
		
    	log.debug("getting " + auctionCount + " auctions");
    	List<Auction> auctions = dao.getAuctions(auctionCount);
    	log.debug("got " + auctions.size() + " auctions");
    	log.debug("with " + getBidCount(auctions) + " bids");
    	
    	cleanupBids(auctions);
		Collection<Account> accounts = getUsersForAuctions(auctions);
    	log.debug("got " + accounts.size() + " accounts");
		Collection<Address> addresses = getAddresses(accounts, auctions);
    	log.debug("got " + addresses.size() + " addresses");
    	Collection<Image> images = dao.getImagesForAuctions(auctions);
    	log.debug("got " + images.size() + " images");
    	
    	dao.clear(); //detatch all from DB before making local changes
    	
    	assignIdsForAuctions(auctions);
    	log.debug("assigned all ids for auctions");
		assignIdsForAccounts(accounts);		
    	log.debug("assigned all ids for accounts");

    	root.getAccount().addAll(accounts);
    	root.getAuction().addAll(auctions);
        root.getAddress().addAll(addresses);
        root.getImage().addAll(images);
		m.marshal(root, writer);
		
		return auctions.size();
	}
	
	private void assignIdsForAuctions(
			Collection<Auction> auctions) {
		for(Auction a : auctions) {
			a.setRefid("auction" + refid++);
			if (a.getShipTo() != null) {
				Address addr = (Address)a.getShipTo();
				if (addr.getRefid() == null || addr.getRefid().length()==0) {
					addr.setRefid("address" + refid++);
				}
			}
		}
	}
	
	private void assignIdsForAccounts(
			Collection<Account> accounts) {
		for(Account a : accounts) {
			a.setRefid("account" + refid++);
			for (Object o : a.getAddress()) {
				Address addr = (Address)o;
				if (addr.getRefid() == null || addr.getRefid().length()==0) {
				    addr.setRefid("address" + refid++);
				}
			}
		}
	}
	
	private Collection<Address> getAddresses(Collection<Account> accounts, 
			Collection<Auction> auctions) {
		Collection<Address> addresses = new HashSet<Address>();
		for (Account acct : accounts) {
			for (Object o : acct.getAddress()) {
			    addresses.add((Address)o);
			}
		}
		for (Auction au : auctions) {
			if (au.getShipTo() != null) {
			    addresses.add((Address)au.getShipTo());
			}
		}
		return addresses;
	}
	
	private Collection<Account> getUsersForAuctions(
			Collection<Auction> auctions) {
		Set<Account> accounts = new HashSet<Account>();
		for(Auction a : auctions) {
			accounts.add(((Account)a.getSeller()));
			Account buyer = (Account)a.getBuyer();
			if (buyer != null) {
				accounts.add(buyer);
			}
			for(Bid bid : a.getBid()) {
				accounts.add(((Account)bid.getBidder()));
			}
		}
		return accounts;
	}
	
	private int getBidCount(Collection<Auction> auctions) {
		int count=0;
		for(Auction a : auctions) {
			count += a.getBid().size();
		}
		return count;
	}
	
	private void cleanupBids(List<Auction> auctions) {
		for(Auction auction : auctions) {
			int bidCount = auction.getBid().size();
			cleanupBids(auction);
			log.debug("action-" + auction.getId() + 
					", bids before=" + bidCount +
					", bids after=" + auction.getBid().size());
		}
		
	}
	/** 
	 * The bids in the database are random values within a range. This 
	 * method will throw away any later bid that is of equal or lesser value
	 * than a time-ordered value.
	 * 
	 * @param auction
	 */
	private void cleanupBids(Auction auction) {
		if (auction.getBid().size() > 0) {
			
			List<Bid> bids = new ArrayList<Bid>();
			Bid prev = null;
			while (auction.getBid().size() > 0) {
				Bid next = getOldest(auction.getBid());
				if (prev == null ||
						next.getAmount() > prev.getAmount()) {					
			        bids.add(next);	
			        prev = next;
				}
				auction.getBid().remove(next);
			}
			auction.getBid().addAll(bids);
		}		
	}
	
	private Bid getOldest(List<Bid> bids) {
		Bid oldest = null;
		for (Bid bid : bids) {
			if (oldest == null || 
					bid.getBidTime().getTime() < oldest.getBidTime().getTime()) {
				oldest = bid;
			}
		}
		return oldest;
	}
	
	public static DataGenerator createDataGenerator(
			Map<String, String> props) throws JAXBException {		
		EntityManagerFactory emf = props != null ?
			Persistence.createEntityManagerFactory(
					JPAESalesDAO.PERSISTENCE_UNIT, props) :
			Persistence.createEntityManagerFactory(
					JPAESalesDAO.PERSISTENCE_UNIT);
		EntityManager em = emf.createEntityManager();
		
		ESalesDAO dao = new JPAESalesDAO();
		((JPAESalesDAO)dao).setEntityManager(em);
		
		DataGenerator gen = new DataGenerator();
		gen.setESalesDAO(dao);
		
		return gen;
	}
	
	public static Map<String, String> getProps(String prefix) {
		Map<String, String> props = new HashMap<String, String>();
		Properties sysProps = System.getProperties();
		for(Iterator<Object> itr=sysProps.keySet().iterator(); itr.hasNext();) {
			String key = (String)itr.next();
			if (key.startsWith(prefix + ".")) {
				String name = key.substring(prefix.length()+1);
				String value = sysProps.getProperty(key);
				props.put(name, value);
			}
		}
		return props;
	}	
}
