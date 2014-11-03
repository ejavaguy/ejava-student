package ejava.examples.webtier.dao;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DAOFactory {
    private static Log log = LogFactory.getLog(DAOFactory.class);
    
    private static HashMap<String, DAOTypeFactory> factories = 
        new HashMap<String, DAOTypeFactory>();
    
    public static DAOTypeFactory getDAOTypeFactory() {
        String name=null;
        Iterator<String> itr = factories.keySet().iterator();
        if (itr.hasNext()) {
            name = factories.keySet().iterator().next();
        }
        log.debug("default DAO Factory name=" + name);
        return (name==null) ? null : factories.get(name);
    }
    
    public static DAOTypeFactory getDAOTypeFactory(String name) {
        return (name==null) ? null : factories.get(name);
    }
    
    public static void registerFactoryType(
            String name, DAOTypeFactory typeFactory){
        log.debug("registering DAOTypeFactory:" + name);
        factories.put(name, typeFactory);
    }
}
