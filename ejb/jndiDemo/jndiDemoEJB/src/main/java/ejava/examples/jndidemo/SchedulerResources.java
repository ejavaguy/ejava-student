package ejava.examples.jndidemo;

import javax.annotation.Resource;

import javax.ejb.EJB;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;


/**
 * This class implements a set of CDI producer fields and methods that are
 * used to inject resources into beans within the application.
 */
public class SchedulerResources {
	/**
	 * Gets a DataSource from the JNDI tree based on a JNDI name
	 * and produces it for any bean injecting a DataSource qualified 
	 * with @JndiDemo
	 */
    @Resource(mappedName="java:jboss/datasources/ExampleDS")
	@Produces
    @JndiDemo
    public DataSource ds;
	
    /**
     * Gets a persistence context based on the persistence unit name and
     * produces it for any bean injecting an EntityManager qualified
     * with a @JndiDemo. 
     */
	@PersistenceContext(unitName="jndidemo")
	@Produces
	@JndiDemo
	public EntityManager em;

	/**
	 * A String for any bean injecting a String qualified by @JndiDemo 
	 * annotation. 
	 */
	@Produces
	@JndiDemo
	public String message="Hello CDI!!!";

	/**
	 * Gets a CookEJB for any bean injecting a Scheduler
	 */
    @EJB(lookup="java:app/jndiDemoEJB/CookEJB!ejava.examples.jndidemo.ejb.CookLocal")
    @Produces
    @Cook
	public Scheduler cook; 
}
