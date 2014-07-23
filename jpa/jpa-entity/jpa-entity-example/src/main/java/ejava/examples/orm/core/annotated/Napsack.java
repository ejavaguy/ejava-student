package ejava.examples.orm.core.annotated;

import javax.persistence.*;

/**
 * This class provides an example of embedding a primary key class within
 * the containing class.
 */
@Entity
@Table(name="ORMCORE_NAPSACK")
public class Napsack {
    @EmbeddedId
    private NapsackPK pk;
    private int size;
    
    public Napsack() {}
    public Napsack(String make, String model) {
        this.pk = new NapsackPK(make, model);
    }
    
    public NapsackPK getPk() { return pk; }
    
    public int getSize() { return size; }
    public void setSize(int size) {
        this.size = size;
    }
    
    public String toString() {
        return new StringBuilder()
           .append(super.toString())	       
           .append(", pk=").append(pk)
           .append(", size=").append(size)
           .toString();
    }
}
