package ejava.examples.cdiconfig.ejbclient;

import java.io.InputStream;



import javax.naming.InitialContext;
import javax.naming.NamingException;


import static org.junit.Assert.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ejava.examples.cdiconfig.bo.Job;
import ejava.examples.cdiconfig.bo.Project;
import ejava.examples.cdiconfig.bo.Task;
import ejava.examples.cdiconfig.ejb.JobsMgmtRemote;
import ejava.examples.cdiconfig.ejb.ProjectsMgmtRemote;
import ejava.examples.cdiconfig.ejb.TasksMgmtRemote;
import ejava.util.ejb.EJBClient;

/**
 * Performs a basic set of calls in the EJBs deployed to demonstrate 
 * aspects of how the EJBs are configured.
 */
public class CdiConfigIT  {
    private static final Logger logger = LoggerFactory.getLogger(CdiConfigIT.class);
    private InitialContext jndi;
    
    static final String EAR_NAME=System.getProperty("ear.name","cdi-config-ear");
    static final String EJB_MODULE=System.getProperty("ejb.module", "cdi-config-ejb");
    static final String tasksMgmtName = System.getProperty("jndi.name.tasksMgmt",
        	EJBClient.getEJBClientLookupName(
    			EAR_NAME, EJB_MODULE, "","TasksMgmtEJB",
    			TasksMgmtRemote.class.getName(), false));
    static final String projectsMgmtName = System.getProperty("jndi.name.projectsMgmt",
            EJBClient.getEJBClientLookupName(
                EAR_NAME, EJB_MODULE, "","ProjectsMgmtEJB",
                ProjectsMgmtRemote.class.getName(), false));
    static final String jobsMgmtName = System.getProperty("jndi.name.jobsMgmt",
            EJBClient.getEJBClientLookupName(
                EAR_NAME, EJB_MODULE, "","JobsMgmtEJB",
                JobsMgmtRemote.class.getName(), false));
    
    @BeforeClass
    public static void setUpClass() throws Exception {
    	    logger.info("*** setUpClass() ***");
		//give application time to fully deploy
		if (Boolean.parseBoolean(System.getProperty("cargo.startstop", "false"))) {
			long waitTime=15000;
	    	    logger.info(String.format("pausing %d secs for server deployment to complete", waitTime/1000));
	    	    Thread.sleep(waitTime);
		}
		else {
	    	    logger.info(String.format("startstop not set"));
		}
    }    
    
    @Before
    public void setUp() throws NamingException {
        logger.debug("getting jndi initial context");
        jndi = new InitialContext();
        logger.debug("tasksMgmtName={}", tasksMgmtName);
        logger.debug("projectsMgmtName={}", projectsMgmtName);
        logger.debug("jobsMgmtName={}", jobsMgmtName);
    }
    
    @After
    public void tearDown() throws NamingException {
        jndi.close();
    }

    /**
     * Calls the EJB that is manually configured thru EJB @PostConstruct.
     */
    @Test
    public void tasksMgmt() throws Exception {
        logger.info("*** tasksMgmt ***");
        
        TasksMgmtRemote tm = (TasksMgmtRemote) jndi.lookup(tasksMgmtName);
        logger.debug("{}={}", tasksMgmtName, tm);
        
        Task task = new Task("my task");
        int id = tm.createTask(task);
        assertNotEquals("no id assigned", 0, id);
    }
    

    /**
     * Calls the EJB that is fully injected by CDI
     */
    @Test
    public void projectsMgmt() throws Exception {
        logger.info("*** projectsMgmt ***");
        
        ProjectsMgmtRemote pm = (ProjectsMgmtRemote) jndi.lookup(projectsMgmtName);
        logger.debug("{}={}", projectsMgmtName, pm);
        
        Project project = new Project("my project");
        int id = pm.createProject(project);
        assertNotEquals("no id assigned", 0, id);
    }

    /**
     * Calls the EJB that uses a separate CDI factory
     */
    @Test
    public void jobsMgmt() throws Exception {
        logger.info("*** jobsMgmt ***");
        
        JobsMgmtRemote jm = (JobsMgmtRemote) jndi.lookup(jobsMgmtName);
        logger.debug("{}={}", jobsMgmtName, jm);
        
        Job job = new Job("my job");
        int id = jm.createJob(job);
        assertNotEquals("no id assigned", 0, id);
    }
}
