package ejava.examples.asyncmarket;

import java.util.List;

import ejava.examples.asyncmarket.bo.AuctionItem;
import ejava.examples.asyncmarket.bo.Order;

public interface Buyer {
    List<AuctionItem> getAvailableItems(int index, int count)
        throws MarketException;
    AuctionItem getItem(long itemId)
        throws MarketException;
    long bidProduct(long productId, String userId, double amount)
        throws MarketException;    
    long placeOrder(long productId, String userId, double maxAmount)
        throws MarketException;
    Order getOrder(long orderId)
        throws MarketException;
}
