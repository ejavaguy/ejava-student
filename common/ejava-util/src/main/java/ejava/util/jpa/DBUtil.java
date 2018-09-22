package ejava.util.jpa;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class will execute a set of drop and create DDL scripts against
 * a supplied entity manager.
 */
public class DBUtil {
    static Logger logger = LoggerFactory.getLogger(DBUtil.class);
    protected EntityManager em;
    protected List<String> dropPaths=new LinkedList<>();
    protected List<String> createPaths=new LinkedList<>();
    
    public DBUtil() {}
    public DBUtil(EntityManager em, String dropPath, String createPath) {
        setEntityManager(em);
        addDropPath(dropPath);
        addCreatePath(createPath);
    }

    public void setEntityManager(EntityManager em) {
        this.em = em; 
    }
    public void addDropPath(String dropPath) {
        this.dropPaths.add(dropPath);
    }
    public void addCreatePath(String createPath) {
        this.createPaths.add(createPath);
    }
    
    /**
     * Locate the input stream either as a file path or a resource path.
     * @param path
     * @return input stream
     * @throws IllegalArgumentException if path not found 
     */
    protected InputStream getInputStream(String path) throws IllegalArgumentException {
        InputStream is = null;
        
        File pathFile = new File(path);
        //try file system first
        if (pathFile.exists()) {
            try {
                is = new FileInputStream(pathFile);
            } catch (FileNotFoundException ex) {
                throw new IllegalArgumentException(String.format("unable to open file %s", path));                
            }
        }
        //then try classpath
        else {
           if ((is = Thread.currentThread()
                      .getContextClassLoader()
                      .getResourceAsStream(path)) == null) {
               throw new IllegalArgumentException(String.format("%s not found in classpath", path));
           }
        }
        
        return is; //let a null inputStream be returned
    }
    
    /**
     * Turn the InputStream into a String for easier parsing for SQL statements.
     * @param is
     * @return string contents of SQL file
     * @throws IOException 
     */
    protected String getString(InputStream is) throws IOException {
        StringBuilder text = new StringBuilder();
        byte[] buffer = new byte[4096];
        for (int n; (n = is.read(buffer)) != -1;) {
            text.append(new String(buffer, 0,n));
        }
        return text.toString();
    }
        
    /**
     * Get list of SQL statements from the supplied string.
     * @param contents
     * @return list of distinct statements
     * @throws Exception
     */
    protected List<String> getStatements(String contents) {
        List<String> statements = new ArrayList<String>();

        for (String tok: contents.split(";")) {
            statements.add(tok.trim());
        }
        return statements;
    }
    
    /**
     * Execute the SQL statements contained within the resource that is located
     * by the path supplied. This path can be either a file path or resource 
     * path.
     * @param path
     * @return count of statements executed
     * @throws Exception
     */
    public int executeScript(String path) {
        if (path == null || path.length() == 0) {
            throw new IllegalStateException("no path provided");
        }
        
        InputStream is = getInputStream(path);
        if (is == null) {
            throw new IllegalStateException("path not found:" + path);
        }
        
        try {
            String sql = getString(is);
            List<String> statements = getStatements(sql);
            logger.debug("found {} statements", statements.size());
            
            for (String statement : statements) {
                logger.debug("executing:" + statement);
                em.createNativeQuery(statement).executeUpdate();    
            }

            return statements.size(); 
        } catch (IOException ex) {
            throw new IllegalStateException("error parsing SQL file", ex);
        }
    }

    /**
     * Return the current nextVal for each sequence used by application.
     * @return map of sequence nextVals
     */
    public Map<String, Number> getSequenceNextVals() {
        //find the names of sequences used
        @SuppressWarnings("unchecked")
        List<String> sequenceNames = em.createNativeQuery(
                "SELECT sequence_name FROM INFORMATION_SCHEMA.SEQUENCES "
                + "where sequence_name not like 'SYSTEM%'")
                .getResultList();
        
        Map<String, Number> sequenceVals = new HashMap<>();
        //query for the next value that would be reported
        for (String sequenceName: sequenceNames) {
            Number nextValue=(Number)em.createNativeQuery(
                    String.format("call nextval('%s')", sequenceName))
                    .getSingleResult();
          sequenceVals.put(sequenceName, nextValue);            
        }
                
        return sequenceVals;
    }
    
    /**
     * Updates the state of the provided sequence(s) to values provided
     * @param sequenceVals
     */
    public void setSequenceNextVals(Map<String, Number> sequenceVals) {
        for (Entry<String, Number> sequence: sequenceVals.entrySet()) {
            em.createNativeQuery(
                    String.format("alter sequence %s restart with ?", sequence.getKey()))
                .setParameter(1, sequence.getValue()).executeUpdate();
        }        
    }
    
    /**
     * Execute the drop script against the DB.
     * @return map of sequence previous nextVal state
     * @throws RuntimeException on errors like schema does not currently 
     * exist. In that case the caller must rollback the current transaction
     * and begin a new one for the create();
     */
    public Map<String, Number> dropAll() throws RuntimeException {
        try {
            Map<String, Number> sequenceVals = getSequenceNextVals();
            for (String script: dropPaths) {
                executeScript(script);
            }
            return sequenceVals;
        } catch (Exception ex) {
            throw new RuntimeException("error dropping DB, might not exist: " + ex);
        }
    }

    /**
     * Execute the create script against the DB. Follow-up by altering the provided
     * sequences to be their previous nextVal state. This is necessary when resetting schema
     * while JPA provider has cached a block of sequence values from a previous execution.
     * @return count of statements executed
     * @throws 
     */
    public int createAll(Map<String, Number> sequenceVals) throws RuntimeException {
        int count=0;
        for (String script: createPaths) {
            count += executeScript(script);
        }
        setSequenceNextVals(sequenceVals);
        return count;
    }    
}