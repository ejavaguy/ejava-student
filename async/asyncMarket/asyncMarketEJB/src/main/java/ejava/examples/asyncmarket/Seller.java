package ejava.examples.asyncmarket;

import ejava.examples.asyncmarket.bo.AuctionItem;
import ejava.examples.asyncmarket.ejb.ResourceNotFoundException;

public interface Seller {
    long sellProduct(String sellerId, AuctionItem item) throws ResourceNotFoundException;
    AuctionItem getItem(long id) throws ResourceNotFoundException;   
}
