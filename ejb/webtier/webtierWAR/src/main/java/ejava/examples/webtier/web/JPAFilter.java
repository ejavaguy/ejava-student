package ejava.examples.webtier.web;

import java.io.IOException;
import java.util.HashMap;

import javax.naming.Context;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JPAFilter implements Filter {
    private static Logger logger = LoggerFactory.getLogger(JPAFilter.class);
    private static final String PU_NAME = "webtier";
    private boolean containerManaged=false;
    @PersistenceUnit(unitName=PU_NAME)
    private EntityManagerFactory emf;
    private static ThreadLocal<EntityManager> em = new ThreadLocal<>();
    
    public void init(FilterConfig config) throws ServletException {
        logger.info("filter initializing JPA DAOs, em=f{}", emf);
        System.out.println(String.format("filter initializing JPA DAOs, em=%s", emf));
        
        if (emf==null) {
            emf = Persistence.createEntityManagerFactory(PU_NAME);
        } else {
            containerManaged=true;
        }
    }    

    public void doFilter(ServletRequest request, 
            ServletResponse response, 
            FilterChain chain) throws IOException, ServletException {
        
        logger.debug("injected entity manager={}", emf);
        EntityManager entityMgr = initEntityManager();               

        EntityTransaction tx = entityMgr.getTransaction();
        if (!tx.isActive()) {
            logger.debug("filter: beginning JPA transaction");
            tx.begin();
        }
        
        chain.doFilter(request, response);

        if (tx.isActive()) {
            if (tx.getRollbackOnly()==true) {
                logger.debug("filter: rolling back JPA transaction");
                tx.rollback();
            }
            else {
                logger.debug("filter: committing JPA transaction");
                tx.commit();
            }
        }
        else {
            logger.debug("filter: no transaction was active");
        }
        
        closeEntityManager();        
    }

    public void destroy() {
        if (!containerManaged) {
            emf.close();
        }
    }

    private EntityManager initEntityManager() throws ServletException {
        EntityManager entityMgr = getEntityManager();
        if (entityMgr==null) {
            entityMgr = emf.createEntityManager();
            em.set(entityMgr);
        }
        return entityMgr;
    }
    
    private void closeEntityManager() {
        EntityManager entityMgr = getEntityManager();
        if (entityMgr!=null) {
            entityMgr.close();
            em.set(null);
        }        
    }
    
    public static final EntityManager getEntityManager() {
        return em.get();
    }
    
    @SuppressWarnings("unused")
    private void dump(Context context, String name) {
        StringBuilder text = new StringBuilder();
        try {
            doDump(0, text, context, name);
        }
        catch (NamingException ex) {}
        logger.debug(text.toString());
    }

    private void doDump(int level, StringBuilder text, Context context, String name) 
        throws NamingException {
        for (NamingEnumeration<NameClassPair> ne = context.list(name); ne.hasMore();) {
            NameClassPair ncp = (NameClassPair) ne.next();
            String objectName = ncp.getName();
            String className = ncp.getClassName();
            String classText = " :" + className;
            if (isContext(className)) {
                text.append(getPad(level) + "+" + objectName + classText +"\n");
                doDump(level + 1, text, context, name + "/" + objectName);
            } else {
                text.append(getPad(level) + "-" + objectName + classText + "\n");
            }
        }
    }
    
    protected boolean isContext(String className) {
        try {
            Class<?> objectClass = Thread.currentThread().getContextClassLoader()
                    .loadClass(className);
            return Context.class.isAssignableFrom(objectClass);
        }
        catch (ClassNotFoundException ex) {
            //object is probably not a context, report as non-context
            return false;
        }
    }

    protected String getPad(int level) {
        StringBuffer pad = new StringBuffer();
        for (int i = 0; i < level; i++) {
            pad.append(" ");
        }
        return pad.toString();
    }


}
