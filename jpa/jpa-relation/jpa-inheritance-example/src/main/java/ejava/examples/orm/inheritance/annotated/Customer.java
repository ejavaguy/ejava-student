package ejava.examples.orm.inheritance.annotated;

import javax.persistence.*;

/**
 * This class provides an entity sub-class example for a join inheritance
 * strategy. The parent class will define a table and primary key value.
 * This class and all derived classes will form separate tables that are joined
 * by primary key.
 */
@Entity 
@Table(name="ORMINH_CUSTOMER") //joined with Person table to form Customer
public class Customer extends Person {
    public enum Rating { GOLD, SILVER, BRONZE }
    @Enumerated(EnumType.STRING)
    private Rating rating;
    
    public Customer() {}
    public Customer(long id) { super(id); }

    public Rating getRating() { return rating; }
    public void setRating(Rating rating) {
        this.rating = rating;
    }
    
    public String toString() {
        StringBuilder text = new StringBuilder(super.toString());
        text.append(", rating=" + rating);
        return text.toString();
    }
}
