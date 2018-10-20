package ejava.examples.cdiconfig.ejb;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.sql.DataSource;

import ejava.examples.cdiconfig.CdiDemo;

//@Stateless
public class TrainSchedulerEJB {

//    public String getName() { return TrainSchedulerEJB.class.getSimpleName(); }

    /**
     * This will inject a persistence context using a textual name qualifier 
     */
//    @Inject @CdiDemo
//    private EntityManager em;
    
    /**
     * This will inject a javax.sql.DataSource
     */
//    @Inject @CdiDemo
//    private DataSource ds;
    
    /**
     * This will inject a DAO by interface type
     */
//    @Inject
//    protected SchedulerDAO schedulerDAO;
    
    /**
     * This will inject a DAO by class type
     */
//    @Inject
//    protected SchedulerDAOImpl jpaSchedulerDAOImpl;

    /**
     * This will inject an EJB based on an Annotation qualifier
     */
//    @Inject @Cook
//    protected Scheduler cook; 

    /**
     * This will inject the ability to get an Scheduler when the qualifier is 
     * known at runtime.
     */
//    @Inject @Any
//    protected Instance<Scheduler> anyCook;
    
    /**
     * This will be looked up at runtime during the @PostConstruct
     */
//    protected Scheduler cook2; 

    /**
     * This will inject a String based on an annotation qualifier
     */
//    @Inject @CdiDemo
//    String message;

//    @Resource
//    protected void setSessionContext(SessionContext ctx) {
//        super.ctx = ctx;
//    }

    @PostConstruct
    public void init() {        
//        log.info("******************* TrainScheduler Created ******************");
//        log.debug("ctx=" + ctx);
//        log.debug("em=" + em);
//        log.debug("ds=" + ds);
//        log.debug("message=" + message);
//        log.debug("cook=" + cook);
//        cook2 = anyCook.select(new CookQualifier()).get();
//        log.debug("cook2=" + cook2);
//        log.debug("schedulerDAO=" + schedulerDAO);
//        log.debug("jpaSchedulerDAOImpl=" + jpaSchedulerDAOImpl);
    }
}
