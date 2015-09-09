package ejava.projects.esales.dao;

import java.util.List;


import ejava.projects.esales.dto.Auction;
import ejava.projects.esales.dto.Image;

public interface ESalesDAO {	
	public List<Auction> getAuctions(int count);
	public List<Image> getImagesForAuctions(List<Auction> auctions);
	public void clear(); 
}
