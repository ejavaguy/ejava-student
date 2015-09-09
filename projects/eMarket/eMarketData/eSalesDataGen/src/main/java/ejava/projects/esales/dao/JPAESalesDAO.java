package ejava.projects.esales.dao;

import java.util.ArrayList;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.projects.esales.dto.Auction;
import ejava.projects.esales.dto.Image;

public class JPAESalesDAO implements ESalesDAO {
	private static final Log log = LogFactory.getLog(JPAESalesDAO.class);
	public static final String PERSISTENCE_UNIT = "eSalesData";
	private EntityManager em;

	public void setEntityManager(EntityManager em) {
		this.em = em;
	}
	
	public void clear() {
		em.clear();
	}
	
	/**
	 * This method will attempt to get auctions with images first and then
	 * auctions without images up to count.
	 */
	@SuppressWarnings("unchecked")
	public List<Auction> getAuctions(int count) {
		//first get auctions that have images
		Query query = em.createQuery(
				"select i.auction from Image i " +
				"order by i.auction.id");
		if (count >= 0) {
		    query.setMaxResults(count);
		}
		List<Auction> auctions = query.getResultList();
		
		//now augment that collection with ones that don't have images
		query = em.createQuery(
				"select DISTINCT au from Auction au " +
				"where au NOT in (select i.auction from Image i)" + 
				"order by au.id");
		if (count > 0 && auctions.size() < count) {
	        query.setMaxResults(count - auctions.size());
	        auctions.addAll(query.getResultList());
		}
		else if (count < 0){
	        auctions.addAll(query.getResultList());
		}
		
		return (List<Auction>)auctions;
	}


	/**
	 * This method will return all images for specified auctions. We have to 
	 * do this in iterations so that we don't exceed the maximum list size in 
	 * a select.
	 * 
	 * @param auctions
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Image> getImagesForAuctions(List<Auction> auctions) {
		
		List<Image> images = new ArrayList<Image>();
    	for (int pos=0; pos < auctions.size(); pos += 100) {
    		int max = (auctions.size() - pos > 100) ?
    				100 : auctions.size() - pos;
    		String list = createSubselectInt(auctions, pos, max); 
    		log.debug("looking for images for auctions:" + list);
			Query query = em.createQuery(
					"select i from Image i " +
					"where i.auction in (" + 
					list + ")")
					.setFirstResult(pos);
			images.addAll(query.getResultList());
    	}
    	return images;
	}

	private static String createSubselectInt(
			List<Auction> auctions, int index, int count) {
		StringBuilder text = new StringBuilder();
		int pos=0;
		int num=0;
		for (Auction a : auctions) {
			if (pos++ >= index) {
				if (text.length() > 0) {
					text.append(",");
				}
				text.append(a.getId());
				if (++num >= count) { break; }
			}
		}
		return text.toString();
	}
}
