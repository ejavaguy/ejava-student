package ejava.examples.txhotel.ejb;

import java.rmi.RemoteException;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.EJBException;
import javax.ejb.Remove;
import javax.ejb.SessionContext;
import javax.ejb.SessionSynchronization;
import javax.ejb.StatefulTimeout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.examples.txhotel.bl.HotelReservationException;
import ejava.examples.txhotel.bl.HotelReservationSession;
import ejava.examples.txhotel.blimpl.HotelReservationSessionImpl;
import ejava.examples.txhotel.bo.Person;
import ejava.examples.txhotel.bo.Reservation;

/**
 * This class provides an example of a stateful session bean wrapper around
 * stateless server calls. In this example case, the user wishes to make 
 * several reservations. However, the stateless session bean will commit
 * them to the database one at a time. This wrapper shows how someone could
 * temporarily store them on the server while the entire set is being 
 * sanity checked. Once they are good to go, a single transaction is started
 * by the commit() method to invoke the stateless session bean for each 
 * transaction. We can easily find tons of fault and areas for improvement with 
 * the actual business logic. However, the basic concept is that the stateful
 * session bean can perform some of the actions of the stateless session bean
 * outside of a transaction and then commit the changes as one unit when ready.
 * The instance is thrown away no matter what happens. We could just as easily
 * keep it around and add some edit functions to fix the erroneous information
 * that caused the rollback.<p/>
 * 
 * This bean is deployed 3 times; default configuration, using a transaction 
 * Required stateless session bean, and using a tranaction RequiresNew 
 * stateless session bean. The test client will try all 3. You'll notice
 * that the one with RequiresNew, will not be able to rollback successful
 * reservations because it operates in a separate transaction. The one with
 * tranaction Required allows all to be rolled back because it joins this
 * tranaction.<p/>
 * 
 * This bean is also configured quite a bit from the ejb-jar.xml file; 
 * using less annotations then the other stateless session bean example.<p/>
 * 
 * It implements the javax.ejb.SessionSynchronization interface so that it
 * can listen, impact, or react to the state of the active transaction.
 *
 * @author jcstaff
 */
//@Stateful - will be supplied in ejb-jar.xml due to multi deploy
@StatefulTimeout(value=3, unit=TimeUnit.MINUTES)
public class HotelReservationSessionEJB
    implements HotelReservationSessionLocal, HotelReservationSessionRemote,
    SessionSynchronization {
    private static final Log log = 
        LogFactory.getLog(HotelReservationSessionEJB.class);
    private HotelReservationSession impl;
    
    @Resource
    private SessionContext ctx;
    
    //injected through deployment descriptor
    private HotelRegistrationLocal reservationist;
    
    /**
     * This method is called when the bean is instantiated to perform manual
     * initialization. It creates the business logic implementation class,
     * instantiates and assigns a DAO, and assigns the EntityManager from the 
     * DAO(s) to use. This should map the the ejb-jar.xml post-construct
     * element.
     */
    @PostConstruct
    public void init() {
        log.info("*** HotelReservationSessionEJB ***");
        
        //these are injected by container
        log.debug("ctx=" + ctx);
        log.debug("reservationist=" + reservationist);
        
        //we manually create these objects
        impl = new HotelReservationSessionImpl();
        ((HotelReservationSessionImpl)impl).setReservationist(reservationist);
    }
    
    /**
     * This method is called just prior to the bean being destroyed. This
     * should map to the ejb-jar.xml pre-destroy element. 
     */
    @PreDestroy
    public void closing() {
        log.info("*** HotelReservationSessionEJB closing ***");
    }

    /**
     * This method will destroy the stateful instance.
     */
    @Remove
    public void close() {
        log.info("*** HotelReservationSessionEJB close ***");
        impl.close();
    }

    public void createReservation(Person person, Date startDate, Date endDate)
        throws HotelReservationException {
        impl.createReservation(person, startDate, endDate);
    }
    
    public void cancelReservations() throws HotelReservationException {
        impl.cancelReservations();
    }

    public List<Reservation> commit() throws HotelReservationException {
        return impl.commit();
    }

    /**
     * This is called to tell us the transaction has started because we 
     * implement SessionSynchronization interface.
     */
    public void afterBegin()  {
        log.debug("*** Transaction Started ***");        
    }
    /**
     * This is called to give us a chance to complain before the 
     * transaction really commits. 
     */
    public void beforeCompletion() throws EJBException, RemoteException {
        log.debug("*** Transaction about to complete, rollback:" + 
                ctx.getRollbackOnly() + " ***");
    }       
    /**
     * This is called to tell us how the transaction did. 
     */
    public void afterCompletion(boolean status)  {
        log.debug("*** Transaction Completed:" + status + " ***");        
    }
}
