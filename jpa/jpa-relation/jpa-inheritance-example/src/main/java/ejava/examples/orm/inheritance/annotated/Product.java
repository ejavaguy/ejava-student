package ejava.examples.orm.inheritance.annotated;

import javax.persistence.*;

/**
 * This class provides an example base class in a single table inheritance
 * strategy. In this case, a single table is defined to hold the properties of
 * all sub-classes. A discriminator field is necessary to denote the type
 * of each row in the table. Note that the primary key is being generated 
 * here since this class definition is shared among the sub-classes.
 */
@Entity @Table(name="ORMINH_PRODUCT")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="PTYPE", //column in root table indicating type
    discriminatorType=DiscriminatorType.STRING,//data type of column
    length=32) //length of discriminator string
public abstract class Product {
    @Id @GeneratedValue
    private long id;
    private double cost;
    
    protected Product() {}
    protected Product(long id) { this.id=id; }
    public long getId() { return id; }

    public double getCost() {
        return cost;
    }
    public void setCost(double cost) {
        this.cost = cost;
    }
    
    @Transient
    public abstract String getName();
    
    public String toString() {
        StringBuilder text = new StringBuilder(super.toString());
        text.append(", id=" + id);
        text.append(", cost=" + cost);
        return text.toString();
    }
}
