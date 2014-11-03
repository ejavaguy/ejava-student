package ejava.examples.asyncmarket.ejb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.examples.asyncmarket.MarketException;
import ejava.examples.asyncmarket.bo.AuctionItem;
import ejava.examples.asyncmarket.bo.Bid;
import ejava.examples.asyncmarket.bo.Person;
import ejava.examples.asyncmarket.dao.PersonDAO;
import ejava.examples.asyncmarket.jpa.JPAPersonDAO;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class UserMgmtEJB implements UserMgmtRemote, UserMgmtLocal {
    private static final Log log = LogFactory.getLog(UserMgmtEJB.class);
    @PersistenceContext(unitName="asyncMarket")
    private EntityManager em;
    
    private PersonDAO userDAO;
    
    @PostConstruct
    public void init() {
        log.info("*** UserMgmtEJB init() ***");
        log.debug("em=" + em);
        
        userDAO = new JPAPersonDAO();
        ((JPAPersonDAO)userDAO).setEntityManager(em);
    }

    public long createUser(String userId, String name) throws MarketException {
        try {
            Person user = new Person();
            user.setName(name);
            user.setUserId(userId);
            return userDAO.createPerson(user).getId();
        } 
        catch (Exception ex) {
            log.error("error creating user", ex);
            throw new MarketException("error creating user:" + ex);
        }
    }

    public List<Person> getUsers(int index, int count) throws MarketException {
        try {
            return makeDTO(userDAO.getPeople(index, count));
        }
        catch (Exception ex) {
            log.error("error getting users", ex);
            throw new MarketException("error getting users:" + ex);
        }
    }

    public Person getUser(long id) throws MarketException {
        try {
            return makeDTO(userDAO.getPerson(id));
        }
        catch (Exception ex) {
            log.error("error getting user", ex);
            throw new MarketException("error getting user:" + ex);
        }
    }

    public Person getUserByUserId(String userId) throws MarketException {
        try {
            Person user = userDAO.getPersonByUserId(userId); 
            log.debug("getUserByUserId(" + userId + ")=" + user);
            return makeDTO(user);
        }
        catch (Exception ex) {
            log.error("error getting user by userId", ex);
            throw new MarketException("error getting user by userId:" + ex);
        }
    }

    public void removeUser(String userId) throws MarketException {
        try {
            Person user = userDAO.getPersonByUserId(userId);
            userDAO.removePerson(user);
        }
        catch (Exception ex) {
            log.error("error getting user by userId", ex);
            throw new MarketException("error getting user by userId:" + ex);
        }
    }
    
    private List<Person> makeDTO(List<Person> people) {
        List<Person> dtos = new ArrayList<Person>();
        for (Person person : people) {
            dtos.add(makeDTO(person));
        }
        return dtos;
    }

    private Person makeDTO(Person user) {
        Person dto = new Person(user.getId());
        dto.setUserId(user.getUserId());
        dto.setName(user.getName());
        dto.setVersion(user.getVersion());
        dto.setItems(makeDTO(user.getItems(), dto));
        return dto;
    }

    private Collection<AuctionItem> makeDTO(
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
            dto.setBids(makeDTO(item.getBids(), dto));            
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
    
    private List<Bid> makeDTO(List<Bid> bids, AuctionItem item) {
        List<Bid> dtos = new ArrayList<Bid>();
        for(Bid bid : bids) {
            dtos.add(makeDTO(bid, item));    
        }
        return dtos;
    }

    private Bid makeDTO(Bid bid, AuctionItem item) {
        Bid dto = new Bid(bid.getId());
        dto.setAmount(bid.getAmount());
        dto.setItem(item);
        item.getBids().add(dto);
        dto.setBidder(makeDTO(bid.getBidder(), dto));
        return dto;
    }

    private Person makeDTO(Person bidder, Bid bid) {
        Person dto = new Person(bidder.getId());
        dto.setUserId(bidder.getUserId());
        dto.setVersion(bidder.getVersion());
        bid.setBidder(dto);
        dto.getBids().add(bid);
        return dto;
    }
}
