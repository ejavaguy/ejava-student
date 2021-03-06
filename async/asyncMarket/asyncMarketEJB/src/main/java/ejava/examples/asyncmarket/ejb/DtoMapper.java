package ejava.examples.asyncmarket.ejb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import ejava.examples.asyncmarket.bo.AuctionItem;
import ejava.examples.asyncmarket.bo.Bid;
import ejava.examples.asyncmarket.bo.Order;
import ejava.examples.asyncmarket.bo.Person;

public class DtoMapper {

    public Bid toDTO(Bid bid) {
        if (bid==null) { return null; }
        Bid dto = new Bid(bid.getId());
        dto.setAmount(bid.getAmount());
        dto.setBidder(toDTO(bid.getBidder(), dto));
        dto.setItem(toDTO(bid.getItem(), dto));
        return dto;
    }
    
    public Person toDTO(Person person, Bid bid) {
        if (person==null) { return null; }
        Person dto = new Person(person.getId());
        dto.setUserId(person.getUserId());
        if (bid!=null) {
            dto.getBids().add(bid);
        }
        bid.setBidder(dto);        
        dto.setVersion(person.getVersion());
        return dto;
    }
    
    public AuctionItem toDTO(AuctionItem item, Bid bid) {
        if (item==null) { return null; }
        AuctionItem dto = new AuctionItem(item.getId());
        dto.setName(item.getName());
        dto.setVersion(item.getVersion());
        dto.setStartDate(item.getStartDate());
        dto.setEndDate(item.getEndDate());
        dto.setMinBid(item.getMinBid());
        if (bid!=null) {
        dto.setWinningBid(bid);
        }
        dto.setClosed(item.isClosed());
        return dto;
    }

    
    public List<AuctionItem> toDTO(List<AuctionItem> items) {
        if (items==null) { return null; }
        return items.stream()
                    .map(item->toDTO(item))
                    .collect(Collectors.toList());
    }

    public AuctionItem toDTO(AuctionItem item) {
        if (item==null) { return null; }
        AuctionItem dto = new AuctionItem(item.getId());
        dto.setVersion(item.getVersion());
        dto.setName(item.getName());
        dto.setStartDate(item.getStartDate());
        dto.setEndDate(item.getEndDate());
        dto.setMinBid(item.getMinBid());
        dto.setBids(toDTO(item.getBids(), dto));
        dto.setWinningBid(null);
        dto.setClosed(item.isClosed());
        return dto;
    }

    public List<Bid> toDTO(List<Bid> bids, AuctionItem item) {
        if (bids==null) { return null; }
        List<Bid> dtos = new ArrayList<Bid>();
        for (Bid bid : bids) {
            Bid dto = new Bid(bid.getId());
            dto.setAmount(bid.getAmount());
            dto.setItem(item);
            item.getBids().add(dto);
            dto.setBidder(toDTO(bid.getBidder(),dto));
            dtos.add(dto);
        }
        return dtos;
    }
    
    public Order toDTO(Order order) {
        Order dto = new Order(order.getId());
        dto.setVersion(order.getVersion());
        dto.setMaxBid(order.getMaxBid());
        dto.setBuyer(toDTO(order.getBuyer(), dto));
        dto.setItem(toDTO(order.getItem(), dto));
        return dto;
    }
    
    public Person toDTO(Person buyer, Order order) {
        Person dto = new Person(buyer.getId());
        dto.setVersion(buyer.getVersion());
        dto.setUserId(buyer.getUserId());
        order.setBuyer(dto);
        return dto;
    }
    
    private AuctionItem toDTO(AuctionItem item, Order order) {
        AuctionItem dto = new AuctionItem(item.getId());
        dto.setVersion(item.getVersion());
        dto.setMinBid(item.getMinBid());
        dto.setStartDate(item.getStartDate());
        dto.setEndDate(item.getEndDate());
        dto.setName(item.getName());
        dto.setOwner(toDTO(item.getOwner(), dto));
        dto.setBids(toDTO(item.getBids(), dto));
        dto.setClosed(item.isClosed());        
        return dto;
    }

    public Person toDTO(Person owner, AuctionItem item) {
        Person dto = new Person(owner.getId());
        dto.setVersion(owner.getVersion());
        dto.setUserId(owner.getUserId());        
        dto.getItems().add(item);
        item.setOwner(dto);
        return dto;
    }
    
    public List<Person> toDTOPeople(List<Person> people) {
        List<Person> dtos = new ArrayList<Person>();
        for (Person person : people) {
            dtos.add(toDTO(person));
        }
        return dtos;
    }

    public Person toDTO(Person user) {
        Person dto = new Person(user.getId());
        dto.setUserId(user.getUserId());
        dto.setName(user.getName());
        dto.setVersion(user.getVersion());
        dto.setItems(toDTO(user.getItems(), dto));
        return dto;
    }

    public Collection<AuctionItem> toDTO(
            Collection<AuctionItem> items, Person owner) {
        Collection<AuctionItem> dtos = new ArrayList<AuctionItem>();
        for(AuctionItem item : items) {
            AuctionItem dto = new AuctionItem(item.getId());
            dto.setVersion(item.getVersion());
            dto.setName(item.getName());
            dto.setOwner(owner);
            owner.getItems().add(dto);
            dto.setMinBid(item.getMinBid());
            dto.setProductId(item.getProductId());
            dto.setStartDate(item.getStartDate());
            dto.setEndDate(item.getEndDate());
            dto.setBids(toDTO(item.getBids(), dto));            
            if (item.getWinningBid() != null) {
                for(Bid bid : dto.getBids()) {
                    if (bid.getId() == item.getWinningBid().getId()) {
                        dto.setWinningBid(bid);
                    }
                }
            }
            dto.setClosed(item.isClosed());
            dtos.add(dto);
        }        
        return dtos;
    }
    
//    private List<Bid> toDTO(List<Bid> bids, AuctionItem item) {
//        List<Bid> dtos = new ArrayList<Bid>();
//        for(Bid bid : bids) {
//            dtos.add(toDTO(bid, item));    
//        }
//        return dtos;
//    }

    public Bid toDTO(Bid bid, AuctionItem item) {
        Bid dto = new Bid(bid.getId());
        dto.setAmount(bid.getAmount());
        dto.setItem(item);
        item.getBids().add(dto);
        dto.setBidder(toDTO(bid.getBidder(), dto));
        return dto;
    }

//    private Person toDTO(Person bidder, Bid bid) {
//        Person dto = new Person(bidder.getId());
//        dto.setUserId(bidder.getUserId());
//        dto.setVersion(bidder.getVersion());
//        bid.setBidder(dto);
//        dto.getBids().add(bid);
//        return dto;
//    }
}
