package ejava.examples.orm.core.annotated;

import javax.persistence.*;

/**
 * This class provides an example of embedding a primary key class within
 * the conatining class and doing all the mapping if the PK here instead
 * of the PK class. Note that we are able to better generalize the PK class
 * because of where the mapping is placed.
 */
@Entity
@Table(name="ORMCORE_PEN")
public class Pen {
    @EmbeddedId
    @AttributeOverrides({
        @AttributeOverride(name="make", column=@Column(name="PEN_MAKE")),
        @AttributeOverride(name="model", column=@Column(name="PEN_MODEL"))             
        })
    private MakeModelPK pk;
    private int size;
    
    public Pen() {}
    public Pen(String make, String model) {
        this.pk = new MakeModelPK(make, model);
    }
    
    public MakeModelPK getPk() { return pk; }
    
    public int getSize() { return size; }
    public void setSize(int size) {
        this.size = size;
    }
    
    public String toString() {
        return new StringBuilder()
           .append(super.toString())	       
           .append("pk=").append(pk)
           .append(", size=").append(size)
           .toString();
    }
}
