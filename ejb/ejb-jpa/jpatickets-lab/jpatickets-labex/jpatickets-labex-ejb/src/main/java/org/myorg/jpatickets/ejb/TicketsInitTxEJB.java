package org.myorg.jpatickets.ejb;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.AccessTimeout;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJBException;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;
import javax.transaction.UserTransaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@AccessTimeout(value=1, unit=TimeUnit.SECONDS)
@TransactionManagement(TransactionManagementType.BEAN)
public class TicketsInitTxEJB {
    private static final Logger logger = LoggerFactory.getLogger(TicketsInitEJB.class);
    private static final String PU_NAME = "jpatickets-schemagen-labex";
    
    @PersistenceUnit(unitName=PU_NAME)
    private EntityManagerFactory emf;
    
    @Resource
    private UserTransaction utx;
    
    private String dropScript;
    private String createScript;

    @PostConstruct
    public void init() {
        StringWriter createScriptWriter = new StringWriter();
        StringWriter dropScriptWriter = new StringWriter();
        Map<String, Object> props = new HashMap<String, Object>();
        props.put("javax.persistence.schema-generation.scripts.create-database-schemas", "true");
        props.put("javax.persistence.schema-generation.scripts.action", "drop-and-create");
        props.put("hibernate.id.new_generator_mappings", "true"); //tell JSE schemagen to use SEQUENCE for @Id=AUTO
        props.put("javax.persistence.schema-generation.scripts.create-target", createScriptWriter);
        props.put("javax.persistence.schema-generation.scripts.drop-target", dropScriptWriter);
        Persistence.generateSchema(PU_NAME, props);
        dropScript = dropScriptWriter.toString();
        createScript = createScriptWriter.toString();
        logger.debug("createScript={}", createScript);
        logger.debug("dropScript={}", dropScript);
    }
    
    private List<String> getCommands(String script) {
        List<String> commands = new ArrayList<String>();
        for (String line : script.split("\n")) {
            commands.add(line);
        }
        return commands;
    }

    @javax.ejb.Lock(LockType.WRITE)
    public int dropDB() {
        logger.info("*** dropDB ***");
        int updates=0;
        EntityManager em = emf.createEntityManager();
        try {
            for (String nativeSQLCommand : getCommands(dropScript)) {
                try {
                    utx.begin();
                    em.joinTransaction();
                    updates += em.createNativeQuery(nativeSQLCommand).executeUpdate();
                    utx.commit();
                } catch (Exception ex) {
                    try { utx.rollback(); } catch (Exception ex2) { throw new EJBException(ex2); }
                    logger.warn("error executing SQL:" + ex + ", " + nativeSQLCommand);
                    //keep going
                }
            }
        } finally {
            em.close();
        }
        return updates;
    }
    
    @javax.ejb.Lock(LockType.WRITE)
    public int createDB() {
        logger.info("*** createDB ***");
        int updates=0;
        EntityManager em = emf.createEntityManager();
        try {
            utx.begin();
            em.joinTransaction();
            //all in one transaction
            for (String nativeSQLCommand : getCommands(createScript)) {
                updates += em.createNativeQuery(nativeSQLCommand).executeUpdate();
            }
            utx.commit();
        } catch (Exception ex) {
            logger.warn("error executing SQL:" + ex);
            try { utx.rollback(); } catch (Exception ex2) { throw new EJBException(ex2); }
        } finally {
            em.close();
        }
        return updates;
    }
}
