package ejava.examples.asyncmarket;

import java.util.List;

import ejava.examples.asyncmarket.bo.AuctionItem;
import ejava.examples.asyncmarket.bo.Order;
import ejava.examples.asyncmarket.ejb.InvalidRequestException;
import ejava.examples.asyncmarket.ejb.ResourceNotFoundException;

public interface Buyer {
    List<AuctionItem> getAvailableItems(int index, int count);
    AuctionItem getItem(long itemId)
        throws ResourceNotFoundException;    
    long bidProduct(long productId, String userId, double amount)
        throws ResourceNotFoundException, InvalidRequestException;    
    long placeOrder(long productId, String userId, double maxAmount)
        throws ResourceNotFoundException, InvalidRequestException;    
    Order getOrder(long orderId)
        throws ResourceNotFoundException;    
}
