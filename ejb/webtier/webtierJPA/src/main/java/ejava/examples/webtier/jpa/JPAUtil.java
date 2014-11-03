package ejava.examples.webtier.jpa;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class is used to create and hold open EntityManagerFactory objects 
 * when not running within a Java EE environment.
 *
 * @author jcstaff
 */
public class JPAUtil {
    private static final Log log = LogFactory.getLog(JPAUtil.class);
    private static final Map<String, EntityManagerFactory> factories = 
        new HashMap<String, EntityManagerFactory>();
    private static ThreadLocal<EntityManager> em = 
        new ThreadLocal<EntityManager>();
    private static Properties emfProperties = new Properties();
    
    /**
     * This method defines the properties to pass to createEntityManagerFactory
     */
    public static void setEntityManagerFactoryProperties(Properties props) {
        emfProperties = (Properties)props.clone();
    }
    
    /**
     * This method will create or return the EntityManagerFactory for 
     * the specified persistence unit.
     * 
     * @param puName matches value in persistence.xml
     * @return
     */
    public static EntityManagerFactory getEntityManagerFactory(String puName) {
        EntityManagerFactory emf = factories.get(puName);
        if (emf == null) {
            synchronized(factories) {
                emf = factories.get(puName);
                if (emf == null) {
                    log.debug("creating EntityManagerFactory(" + puName + ")");
                    emf = (emfProperties != null && emfProperties.size() > 0) ?
                            Persistence.createEntityManagerFactory(puName, 
                                    emfProperties) :
                            Persistence.createEntityManagerFactory(puName);                                
                    factories.put(puName, emf);
                }
            }
        }
        return emf;
    }
        
    public static void setEntityManager(EntityManager emgr) {
        em.set(emgr);
    }    

    public static EntityManager getEntityManager(String puName) {
        EntityManager emgr = em.get();
        if (emgr == null) {
            synchronized (em) {
                if (emgr == null) {
                    //okay-nobody gave us one, use JavaSE technique to get one
                    emgr = getEntityManagerFactory(puName).createEntityManager();
                    setEntityManager(emgr);
                }
            }
        }
        return em.get();
    }
    public static EntityManager getEntityManager() {
        EntityManager emgr = em.get();
        if (emgr == null) {
            log.fatal("EntityManager not initialized for Thread!!!");
        }
        return em.get();
    }
    /**
     * This method just allows the caller to look at entity manager to see
     * if it is currently set.
     * @return
     */
    public static EntityManager peekEntityManager() {
        return em.get();
    }
    public static void closeEntityManager() {
        EntityManager emgr = em.get();
        if (emgr != null) {
            em.remove();
            emgr.close();
        }
    }

    
    /**
     * This method closes all EntityManagerFactory objects and clears them
     * from the cache.
     * 
     */
    public static void close() {
        log.debug("closing " + factories.size() + " EntityManagerFactories");
        synchronized(factories) {
            for(String puName : factories.keySet()) {
                factories.get(puName).close();
                log.debug(puName + " closed");
            }
            factories.clear();
        }
    }
    
    
}
