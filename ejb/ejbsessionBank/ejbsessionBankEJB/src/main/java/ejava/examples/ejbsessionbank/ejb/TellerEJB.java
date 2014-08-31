package ejava.examples.ejbsessionbank.ejb;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.examples.ejbsessionbank.bl.BankException;
import ejava.examples.ejbsessionbank.bl.Teller;
import ejava.examples.ejbsessionbank.blimpl.TellerImpl;
import ejava.examples.ejbsessionbank.bo.Account;
import ejava.examples.ejbsessionbank.bo.Ledger;
import ejava.examples.ejbsessionbank.bo.Owner;
import ejava.examples.ejbsessionbank.dao.AccountDAO;
import ejava.examples.ejbsessionbank.dao.DAOException;
import ejava.examples.ejbsessionbank.dao.OwnerDAO;
import ejava.examples.ejbsessionbank.dto.OwnerDTO;
import ejava.examples.ejbsessionbank.jpa.JPAAccountDAO;
import ejava.examples.ejbsessionbank.jpa.JPAOwnerDAO;

/**
 * This class implements a Stateless Session Bean wrapper around the 
 * Teller business logic. With the light-weight EJB3 design for session 
 * beans, there is no reason why the TellerImpl couldn't be the session bean
 * and we could possibly do so through the deployment descriptor. However,
 * adding this specific class permits a clear separation between implementing
 * the business logic and handling RMI, transactions, and security (coming)
 * issues that are part of being a session bean. 
 *
 * @author jcstaff
 */
@Stateless
public class TellerEJB implements TellerLocal, TellerRemote {
    private static final Log log = LogFactory.getLog(TellerEJB.class);

    @Resource
    protected SessionContext ctx;
    
    /** The peristence context will be defined as a property to allow a 
     * derived class to override this value and assist in unit testing.
     */    
    @PersistenceContext(unitName="ejbsessionbank")
    protected EntityManager em;
    
    @Resource(name="daoClass")
    protected String daoClassName;
    
    @EJB
    private StatsLocal stats;

    protected Teller teller;
    
    /**
     * This method performs one-time initialization for the session bean.
     * The name of the DAO class is provided through a JNDI ENC property 
     * value.
     */
    @PostConstruct
    public void init() {
        log.debug("init(), daoClass=" + daoClassName);
        teller = new TellerImpl();
        
        try {
            AccountDAO dao = (AccountDAO)Thread.currentThread()
                                               .getContextClassLoader()
                                               .loadClass(daoClassName)
                                               .newInstance();            
            ((JPAAccountDAO)dao).setEntityManager(em);
            ((TellerImpl)teller).setAcctDAO(dao);
            
            OwnerDAO ownerDAO = new JPAOwnerDAO();
            ((JPAOwnerDAO)ownerDAO).setEntityManager(em);            
            ((TellerImpl)teller).setOwnerDAO(ownerDAO);
        }
        catch (Exception ex) {
            log.fatal("error loading dao class:" + daoClassName, ex);
            throw new EJBException("error loading dao class:" + daoClassName 
               + ", " + ex);
        }
        
    }

    /**
     * This method will perform one-time cleanup for the object. There is 
     * really nothing that needs to be done at this time. The operations 
     * supplied demo some types of cleanup that could be done.
     *
     */
    @PreDestroy
    public void close() {
        log.debug("close");
        teller = null;
    }

    public Account createAccount(String accountNumber) throws BankException {
        debug();
        try {
        	Account account = teller.createAccount(accountNumber);
        	stats.open();
            return account;
        }
        catch (DAOException ex) {
            ctx.setRollbackOnly();
            log.fatal("internal error creating account", ex);
            throw new BankException("internal error creating account:" + ex);
        }
    }
    
    public Account closeAccount(String acctNum) throws BankException {
        debug();
        try {
            Account account = teller.closeAccount(acctNum);
            stats.close();
            return account;
        }
        catch (DAOException ex) {
            ctx.setRollbackOnly();
            log.fatal("internal error closing account", ex);
            throw new BankException("internal error closing account:" + ex);
        }
    }

    public Account getAccount(String acctNum) throws BankException {
        debug();
        try {
            return teller.getAccount(acctNum);
        }
        catch (DAOException ex) {
            ctx.setRollbackOnly();
            log.fatal("internal error getting account", ex);
            throw new BankException("internal error getting account:" + ex);
        }
    }

    public List<Account> getOverdrawnAccounts(int index, int count) throws BankException {
        debug();
        try {
            return teller.getOverdrawnAccounts(index, count);
        }
        catch (DAOException ex) {
            ctx.setRollbackOnly();
            log.fatal("internal getting accounts", ex);
            throw new BankException("internal getting accounts:" + ex);
        }
    }

    public List<Account> getAccounts(int index, int count) throws BankException {
        debug();
        try {
            return teller.getAccounts(index, count);
        }
        catch (DAOException ex) {
            ctx.setRollbackOnly();
            log.fatal("internal getting accounts", ex);
            throw new BankException("internal getting accounts:" + ex);
        }
    }

    public void updateAccount(Account account) throws BankException {
        debug();
        try {
            log.debug("in EJB, about to update account:" + account);
            teller.updateAccount(account);
        }
        catch (DAOException ex) {
            ctx.setRollbackOnly();
            log.fatal("internal error updating account", ex);
            throw new BankException("internal error updating account:" + ex);
        }
    }

    public Ledger getLedger() throws BankException {
        debug();
        try {
            Ledger ledger = new Ledger(
                    teller.getLedgerCount(),
                    teller.getLedgerSum(),
                    teller.getLedgerAveBalance());            
            return ledger;
        }
        catch (DAOException ex) {
            ctx.setRollbackOnly();
            log.fatal("internal error getting ledger", ex);
            throw new BankException("internal error getting ledger:" + ex);
        }
    }

    /**
     * This method takes advantage of the DAO/Business logic knowledge
     * of how to create a DTO. Note that the query is not actually known
     * to the business logic or DAO. It is expressed in an ORM.xml file.
     */
    public Ledger getLedger2() throws BankException {
        debug();
        try {
            return teller.getLedger();
        }
        catch (DAOException ex) {
            ctx.setRollbackOnly();
            log.fatal("internal error getting ledger", ex);
            throw new BankException("internal error getting ledger:" + ex);
        }
    }
    
    /**
     * This method is and example of a method that may be too fine for a 
     * remote method and should be encapsulated in a remote facade call.
     */
    public double getLedgerAveBalance() throws BankException {
        debug();
        try {
            return teller.getLedgerAveBalance();
        }
        catch (DAOException ex) {
            ctx.setRollbackOnly();
            log.fatal("internal error getting ledger ave balance", ex);
            throw new BankException(
                    "internal error getting ledger ave balance:" + ex);
        }
    }

    /**
     * This method is and example of a method that may be too fine for a 
     * remote method and should be encapsulated in a remote facade call.
     */
    public long getLedgerCount() throws BankException {
        debug();
        try {
            return teller.getLedgerCount();
        }
        catch (DAOException ex) {
            ctx.setRollbackOnly();
            log.fatal("internal error getting ledger count", ex);
            throw new BankException(
                    "internal error getting ledger count:" + ex);
        }
    }

    /**
     * This method is and example of a method that may be too fine for a 
     * remote method and should be encapsulated in a remote facade call.
     */
    public double getLedgerSum() throws BankException {
        debug();
        try {
            return teller.getLedgerSum();
        }
        catch (DAOException ex) {
            ctx.setRollbackOnly();
            log.fatal("internal error getting ledger sum", ex);
            throw new BankException(
                    "internal error getting ledger sum:" + ex);
        }
    }
    
    private void debug() {
        if (log.isDebugEnabled()) {
            //nothing yet...
        }
    }
    
    // -- this half was added to provide a richer data model to demo
    // -- lazy load issues.

    public Owner addOwner(long ownerId, String accountNumber)
            throws BankException {
        try {
            log.debug("in EJB, about to add owner to account");
            return teller.addOwner(ownerId, accountNumber);
        }
        catch (DAOException ex) {
            ctx.setRollbackOnly();
            log.fatal("internal error updating account", ex);
            throw new BankException("internal error updating account:" + ex);
        }
    }

    public Owner createOwner(String firstName, String lastName, String ssn)
            throws BankException {
        try {
            log.debug("in EJB, about to create owner:" + firstName);
            return teller.createOwner(firstName, lastName, ssn);
        }
        catch (DAOException ex) {
            ctx.setRollbackOnly();
            log.fatal("internal error updating account", ex);
            throw new BankException("internal error updating account:" + ex);
        }
    }

    public Owner openAccount(long ownerId, String accountNumber)
            throws BankException {
        try {
            log.debug("in EJB, about to open account for owner");
            return teller.openAccount(ownerId, accountNumber);
        }
        catch (DAOException ex) {
            ctx.setRollbackOnly();
            log.fatal("internal error updating account", ex);
            throw new BankException("internal error updating account:" + ex);
        }
    }

    public void removeOwner(long ownerId) throws BankException {
        try {
            log.debug("in EJB, about to remove owner:" + ownerId);
            teller.removeOwner(ownerId);
        }
        catch (DAOException ex) {
            ctx.setRollbackOnly();
            log.fatal("internal error updating account", ex);
            throw new BankException("internal error updating account:" + ex);
        }
    }

    public List<Owner> getOwners(int index, int count) throws BankException {
        try {
            log.debug("in EJB, about to get owners");
            return teller.getOwners(index, count);
        }
        catch (DAOException ex) {
            ctx.setRollbackOnly();
            log.fatal("internal error updating account", ex);
            throw new BankException("internal error updating account:" + ex);
        }
    }

    public List<Owner> getOwnersLoaded(int index, int count) 
        throws BankException {
        List<Owner> owners = getOwners(index, count);
        for(Owner owner : owners) {
            for (Account account : owner.getAccounts()) {
                account.getBalance(); //call a method to get loaded
            }
        }
        return owners;
    }
    
    public List<Owner> getOwnersPOJO(int index, int count) 
        throws BankException {
        List<Owner> ownersPOJO = new ArrayList<Owner>();
        for(Owner owner : getOwners(index, count)) {
            Owner ownerPOJO = new Owner(owner.getId());
            ownerPOJO.setFirstName(owner.getFirstName());
            ownerPOJO.setLastName(owner.getLastName());
            ownerPOJO.setSsn(owner.getSsn());
            for (Account account : owner.getAccounts()) {
                Account accountPOJO = new Account(account.getId());
                accountPOJO.setAccountNumber(account.getAccountNumber());
                accountPOJO.deposit(account.getBalance());
                ownerPOJO.getAccounts().add(accountPOJO);
            }
            ownersPOJO.add(ownerPOJO);
        }
        return ownersPOJO;
    }

    public List<OwnerDTO> getOwnersDTO(int index, int count) 
        throws BankException {
        List<OwnerDTO> ownersDTO = new ArrayList<OwnerDTO>();
        for(Owner owner : getOwners(index, count)) {
            OwnerDTO ownerDTO = new OwnerDTO(owner.getId());
            ownerDTO.setFirstName(owner.getFirstName());
            ownerDTO.setLastName(owner.getLastName());
            ownerDTO.setAccounts(owner.getAccounts().size());
            ownersDTO.add(ownerDTO);
        }
        return ownersDTO;
    }

    @Override
    public String whoAmI() {
    	return ctx.getCallerPrincipal().getName();
    }
}
