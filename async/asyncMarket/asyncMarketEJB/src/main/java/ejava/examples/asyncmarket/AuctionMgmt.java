package ejava.examples.asyncmarket;

import java.util.List;

import javax.ejb.ScheduleExpression;

import ejava.examples.asyncmarket.bo.AuctionItem;
import ejava.examples.asyncmarket.bo.Bid;
import ejava.examples.asyncmarket.ejb.ResourceNotFoundException;

public interface AuctionMgmt {
    void closeBidding(long itemId) throws ResourceNotFoundException;
    Bid getWinningBid(long itemId) throws ResourceNotFoundException;
    int checkAuction();
    
    //cleanup functions
    void removeBid(long bidId) throws ResourceNotFoundException;
    void removeItem(long itemId) throws ResourceNotFoundException;
    List<AuctionItem> getItems(int index, int count);

    
    //infrastructure functions
    void initTimers(long delay);
    void initTimers(ScheduleExpression schedule);
    void cancelTimers();
}
