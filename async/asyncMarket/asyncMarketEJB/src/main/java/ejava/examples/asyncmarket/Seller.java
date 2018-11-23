package ejava.examples.asyncmarket;

import ejava.examples.asyncmarket.bo.AuctionItem;

public interface Seller {
    long sellProduct(String sellerId, AuctionItem item) throws MarketException;
    AuctionItem getItem(long id) throws MarketException;   
}
