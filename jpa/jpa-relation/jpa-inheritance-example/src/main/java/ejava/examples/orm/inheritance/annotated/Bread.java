package ejava.examples.orm.inheritance.annotated;

import java.util.Date;

import javax.persistence.*;

/**
 * This class provides an example of an entity that is part of a single table
 * inheritance approach. In this case, all derived classes will be merged into
 * a single table defined by the parent class. This class defines its 
 * discriminiator value. @see Soup class for a sibling example where the 
 * default value is taken.
 */
@Entity
@DiscriminatorValue("BREAD_TYPE") //value placed in root table to indicate type
public class Bread extends Product {
    private int slices;
    @Temporal(TemporalType.DATE)
    private Date bakedOn;
    
    public Bread() {}
    public Bread(long id) { super(id); }

    public Date getBakedOn() { return bakedOn;}
    public void setBakedOn(Date bakedOn) {
        this.bakedOn = bakedOn;
    }

    public int getSlices() { return slices; }
    public void setSlices(int slices) { 
    	this.slices = slices; 
    }

    @Transient
    public String getName() { return "Bread"; }

    public String toString() {
        StringBuilder text = new StringBuilder(super.toString());
        text.append(", slices=" + slices);
        text.append(", baked=" + bakedOn);
        return text.toString();
    }
}
