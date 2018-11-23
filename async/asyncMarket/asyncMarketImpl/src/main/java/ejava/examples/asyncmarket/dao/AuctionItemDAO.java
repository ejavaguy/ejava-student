package ejava.examples.asyncmarket.dao;

import java.util.List;
import java.util.Map;

import ejava.examples.asyncmarket.bo.AuctionItem;
import ejava.examples.asyncmarket.bo.Bid;

public interface AuctionItemDAO {
    AuctionItem getItem(long itemId);
    AuctionItem createItem(AuctionItem item);
    AuctionItem updateItem(AuctionItem item);
    Bid getBid(long bidId);
    void removeBid(Bid bid);
    void removeItem(long id);
    List<AuctionItem> getItems(int index, int count);
    List<AuctionItem> getAvailableItems(int index, int count);
    List<AuctionItem> getItems(
            String queryString, Map<String, Object> args, int index, int count);
}
