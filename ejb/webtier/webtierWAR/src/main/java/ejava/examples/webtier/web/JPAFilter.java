package ejava.examples.webtier.web;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.examples.webtier.dao.DAOFactory;
import ejava.examples.webtier.dao.DAOTypeFactory;
import ejava.examples.webtier.jpa.JPADAOTypeFactory;
import ejava.examples.webtier.jpa.JPAUtil;

public class JPAFilter implements Filter {
    private static Log log = LogFactory.getLog(JPAFilter.class);
    private String puName = "webtier";
    private Properties emfProperties = new Properties();
    @PersistenceContext(unitName="webtier")
    private EntityManager pc;

    
    public void init(FilterConfig config) throws ServletException {
        log.debug("filter initializing JPA DAOs"); 
        new JPADAOTypeFactory();
        DAOTypeFactory daoType = DAOFactory.getDAOTypeFactory();
        log.debug("filter got typeFactory:" + daoType.getName());
        
        for(Enumeration<?> e=config.getInitParameterNames();
            e.hasMoreElements(); ) {
            String key = (String)e.nextElement();
            String value=(String)config.getInitParameter(key);
            emfProperties.put(key, value);
        }
        log.debug("emfProperties=" + emfProperties);
    }    

    public void doFilter(ServletRequest request, 
            ServletResponse response, 
            FilterChain chain) throws IOException, ServletException {
        
        log.debug("injected entity manager=" + pc);
        EntityManager em = getEntityManager();               

        if (!em.getTransaction().isActive()) {
            log.debug("filter: beginning JPA transaction");
            em.getTransaction().begin();
        }
        
        chain.doFilter(request, response);

        if (em.getTransaction().isActive()) {
            if (em.getTransaction().getRollbackOnly()==true) {
                log.debug("filter: rolling back JPA transaction");
                em.getTransaction().rollback();
            }
            else {
                log.debug("filter: committing JPA transaction");
                em.flush();                
                em.clear();
                em.getTransaction().commit();
            }
        }
        else {
            log.debug("filter: no transaction was active");
        }
    }

    public void destroy() {
        JPAUtil.close();
    }

    private EntityManager getEntityManager() throws ServletException {
        if (JPAUtil.peekEntityManager() == null) {
            JPAUtil.setEntityManagerFactoryProperties(emfProperties);
        }
        return JPAUtil.getEntityManager(puName);
    }        

    
    @SuppressWarnings("unused")
    private Properties getInitialContextProperties() {
       Properties props = new Properties();
       props.put("java.naming.factory.initial", 
               "org.jnp.interfaces.LocalOnlyContextFactory");
       props.put("java.naming.factory.url.pkgs", 
               "org.jboss.naming:org.jnp.interfaces");
       return props;
    }    

    @SuppressWarnings("unused")
    private Properties getEntityManagerFactoryProperties() {
        Properties props = new Properties();
        props.put("hibernate.jndi.naming.factory.initial", 
                "org.jnp.interfaces.LocalOnlyContextFactory");
        props.put("hibernate.jndi.java.naming.factory.url.pkgs", 
                "org.jboss.naming:org.jnp.interfaces");
                
        return props;
     }    
    
    @SuppressWarnings("unused")
    private void dump(Context context, String name) {
        StringBuilder text = new StringBuilder();
        try {
            doDump(0, text, context, name);
        }
        catch (NamingException ex) {}
        log.debug(text.toString());
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
