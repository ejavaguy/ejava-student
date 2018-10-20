package ejava.examples.cdiconfig;

import javax.annotation.Resource;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import ejava.examples.cdiconfig.dao.JobsDao;
import ejava.examples.cdiconfig.dao.JobsDaoImpl;


/**
 * This class implements a set of CDI producer fields and methods that are
 * used to inject resources into beans within the application.
 */
public class CdiDemoConfig {
    /**
     * Gets a persistence context based on the persistence unit name and
     * produces it for any bean injecting an EntityManager qualified
     * with a @CdiDemo. 
     */
    @Produces
    @CdiDemo
    @PersistenceContext(unitName="cdi-config")
    public EntityManager em;
    
    /**
     * Performs a JNDI lookup and makes available as an injectable bean
     */
    @Produces
    @Resource(lookup="java:jboss/datasources/ExampleDS")
    public DataSource ds;

    /**
     * A String for any bean injecting a String qualified by @CdiDemo 
     * annotation. 
     */
    @Produces
    @CdiDemo
    public String message="Hello CDI!!!";
	
	
    @Produces
    //@CdiDemo
    public JobsDao jobsDao(@CdiDemo EntityManager em) {
        JobsDaoImpl impl = new JobsDaoImpl();
        impl.setEntityManager(em);
        return impl;
    }

    /**
     * Gets a EJB for any bean injecting a Scheduler
    @EJB(lookup="java:app/jndiDemoEJB/CookEJB!ejava.examples.cdiconfig.ejb.CookLocal")
    @Produces
    @Cook
    public ProjectMgmt projectMgmt; 
     */
}
