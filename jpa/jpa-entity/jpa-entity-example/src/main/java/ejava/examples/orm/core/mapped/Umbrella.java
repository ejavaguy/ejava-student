package ejava.examples.orm.core.mapped;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class provides an example of using LAZY fetch hints. We use 
 * log statements in the setters and getters to track activity within
 * the objects.
 */
public class Umbrella {
    private static Log log = LogFactory.getLog(Umbrella.class);
    private long id;
    private String make;
    private String model;
    
    public Umbrella() {
        trace("ctor()");
    }
    public Umbrella(long id) {
        trace("ctor(" + id + ")");
        this.id = id; 
    }
    
    public long getId() {
        trace("getId()=" + id);
        return id;
    }
    protected void setId(long id) {
        trace("setId(" + id + ")");
        this.id = id;
    }
    public char[] getMake() {
        trace("getMake()=" + make);
        return make.toCharArray();
    }
    public void setMake(char[] make) {
        trace("setMake(" + new String(make) + ")");
        this.make = new String(make);
    }
    public String getModel() {
        trace("getModel()=" + model);
        return model;
    }
    public void setModel(String model) {
        trace("setModel(" + model + ")");
        this.model = model;
    }

    public String toString() {
        return super.toString() +
           "make=" + make +
           ", model=" + model;
    }
    private void trace(String message) {
        log.debug(super.toString() + ":" + message);
    }
}
