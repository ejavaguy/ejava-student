package ejava.examples.asyncmarket;

import ejava.examples.asyncmarket.bo.AuctionItem;

/**
 * @author jcstaff
 */
public interface Seller {
    long sellProduct(String sellerId, AuctionItem item) throws MarketException;
    AuctionItem getItem(long id) throws MarketException;   
}
