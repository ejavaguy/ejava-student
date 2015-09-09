package ejava.projects.esales.xml;

import ejava.projects.esales.dto.Account;

import ejava.projects.esales.dto.Address;
import ejava.projects.esales.dto.Auction;
import ejava.projects.esales.dto.Bid;
import ejava.projects.esales.dto.ESales;

import java.util.Date;

public class SampleGen {
	static int addressId = 0;
	static int auctionId = 0;
	static int bidId = 0;
	static int accountId = 0;
    public ESales createSales() throws Exception {
        ESales sales = new ESales();
        
        Account seller = createAccount("Joe", "Charles", "Seller");
        Account bidder1 = createAccount("Jill", "Bean", "Bidwell");
        Account bidder2 = createAccount("Dan", "Didly", "Doneright");
        sales.getAccount().add(seller);
        sales.getAccount().add(bidder1);
        sales.getAccount().add(bidder2);
        
        Auction auction = createAuction(seller, bidder1, bidder2);
        sales.getAuction().add(auction);
        
        for (Account account : sales.getAccount()) {
        	for (Object object : account.getAddress()) {
        		sales.getAddress().add((Address)object);
        	}
        }
        for (Auction a : sales.getAuction()) {
        	for (Bid bid : a.getBid()) {
        		sales.getBid().add(bid);
        	}
        }
        return sales;
    }

    public Auction createAuction(Account seller, Account...bidder) {
    	Auction auction = new Auction();
    	auction.setId(auctionId);
    	auction.setRefid("auction:"+auctionId++);
    	auction.setAskingPrice(1.00F);
    	auction.setBuyer(bidder[0]);
    	auction.setCategory("exampleCategory");
    	auction.setEndTime(new Date());
    	auction.setStartTime(
			new Date(auction.getEndTime().getTime()-3*3600*1000));
    	auction.setPurchasePrice(auction.getAskingPrice());
    	auction.setSeller(seller);
    	auction.setShipTo(seller.getAddress().get(0));
    	auction.setTitle("example item");
    	for (Account account : bidder) {
    		Bid bid = new Bid();
    		bid.setAmount(1.00F);
    		bid.setBidder(account);
    		bid.setId(bidId++);
    		bid.setItem(auction);
    		auction.getBid().add(bid);
    	}
    	return auction;
	}

	public Address createAddress1(String addressee) {
    	Address address = new Address();
    	address.setId(addressId);
    	address.setRefid("address:" + addressId++);
    	address.setAddressee(addressee);
    	address.setCity("Acity");
    	address.setName("primary");
    	address.setState("EX");
    	address.setStreet("Example");
    	address.setZip("12345");
    	return address;
    }
    
    public Account createAccount(
    		String firstName, String middleName, String lastName) {
    	Account account = new Account();
    	account.setRefid("account:" + accountId++);
    	account.setEmail(firstName + "." + lastName + "@example.com");
    	account.setEndDate(null);
    	account.setFirstName(firstName);
    	account.setMiddleName(middleName);
    	account.setLastName(lastName);
    	account.setLogin(
    			firstName.substring(0, 1).toLowerCase() + 
    			middleName.substring(0, 1).toLowerCase() +
    			lastName.toLowerCase());
    	account.setStartDate(new Date());
    	    	
    	account.getAddress().add(
    			createAddress1(firstName + " " + lastName));
    	return account;
    }

}
