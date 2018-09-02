package ejava.examples.jndidemo;

/**
 * This interface defines some key debug methods we need to inspect the
 * Session Beans that we are configuring. 
 */
public interface Scheduler {
    /** return the name of the bean */
    String getName();
    
    /** return the toString() of the object at the given JNDI name */
    String getJndiProperty(String name);
    
    /** return the toString() of the object at the given Context.lookup name */
    String getCtxProperty(String name);  
    
   /** return a string representation of java:comp/env */
    String getEnv();
}
