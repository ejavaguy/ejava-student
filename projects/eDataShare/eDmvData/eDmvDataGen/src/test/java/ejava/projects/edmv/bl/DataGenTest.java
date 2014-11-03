package ejava.projects.edmv.bl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.projects.edmv.dao.DMVPersonDAO;
import ejava.projects.edmv.dao.DMVVehicleDAO;
import ejava.projects.edmv.jpa.JPADMVPersonDAO;
import ejava.projects.edmv.jpa.JPADMVVehicleDAO;

import junit.framework.TestCase;

/**
 * This class will establish a connection to the data source and
 * then configure the DataGen to export the contents of the DB
 * to a test directory as XML. Note that this class requires access
 * to a specific DB instance and is not portable outside of the
 * course authoring environment. 
 * 
 * @author jcstaff
 *
 */
public class DataGenTest extends TestCase {
    protected Log log = LogFactory.getLog(DataGenTest.class);
    protected static String PERSISTENCE_UNIT = "eDmvData";
    protected static String outputDirName = 
        System.getProperty("outputDir");
    protected static File outputDir;
    
    protected EntityManager em;
    protected DMVPersonDAO personDAO;
    protected DMVVehicleDAO vehicleDAO;
    
    public void setUp() {
        assertNotNull("outputDir not supplied", outputDirName);
        outputDir = new File(outputDirName);
        assertTrue("outputDir does not exist:" + outputDir,
                outputDir.exists());
        
        EntityManagerFactory emf = 
            Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
        em = emf.createEntityManager();
        
        personDAO = new JPADMVPersonDAO();
        ((JPADMVPersonDAO)personDAO).setEntityManager(em);
        
        vehicleDAO = new JPADMVVehicleDAO();
        ((JPADMVVehicleDAO)vehicleDAO).setEntityManager(em);
    }
    public void tearDown() {
        em.close();
    }
    
    public void testGenerate() throws Exception {
        log.info("*** generate ***");
        DataGen dataGen = new DataGen();
        dataGen.setPersonDAO(personDAO);
        dataGen.setVehicleDAO(vehicleDAO);
        
        File outputFile = new File(outputDir, "dmv-all.xml");
        OutputStream os = new FileOutputStream(outputFile);
        
        log.info("generating file:" + outputFile);
        Writer w = new OutputStreamWriter(os);
        dataGen.generate(w);
        w.close();
        
        log.info("generation complete: " + outputFile);
    }
}
