package ejava.examples.orm.core.annotated;

import javax.persistence.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides an example of using LAZY fetch hints. We use 
 * log statements in the setters and getters to track activity within
 * the objects.
 */
@Entity
@Table(name="ORMCORE_UMBRELLA")
public class Umbrella {
    private static final Logger log = LoggerFactory.getLogger(Umbrella.class);
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
    
    @Id
    public long getId() {
        trace("getId()=" + id);
        return id;
    }
    @SuppressWarnings("unused")
    private void setId(long id) {
        trace("setId(" + id + ")");
        this.id = id;
    }
    
    @Lob
    @Basic(fetch=FetchType.LAZY)
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
        return new StringBuilder()
           .append(super.toString())	       
           .append("make=").append(make)
           .append(", model=").append(model)
           .toString();
    }
    private void trace(String message) {
        log.debug(super.toString() + ":" + message);
    }

}
