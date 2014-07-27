package ejava.jpa.examples.query;

import javax.persistence.*;

@Entity 
@Table(name="JPAQL_CUSTOMER")
@NamedQueries({
    @NamedQuery(name="Customer.getCustomersByName",
            query="select c from Customer c " +
                    "where c.firstName like :first AND c.lastName like :last"),
    @NamedQuery(name="Customer.getCustomerPurchases",
            query="select s from Sale s " +
                    "where s.buyerId=:custId")
})
/* issues compiling this with hibernate3-plugin
@NamedNativeQueries({
	@NamedNativeQuery(name="Customer.getCustomerRows", 
			query="select * from JPAQL_CUSTOMER c " +
                  "where c.FIRST_NAME = ?1")
})
*/
public class Customer {
    @Id @GeneratedValue 
    @Column(name="CUSTOMER_ID")    
    private long id;
    
    @Column(name="FIRST_NAME", length=16, nullable=true)
    private String firstName;
    
    @Column(name="LAST_NAME", length=16, nullable=true)
    private String lastName;
    
    public long getId() { return id; }

    public String getFirstName() { return firstName; }
    public Customer setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }
    
    public String getLastName() { return lastName; }
    public Customer setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }
    
    public String toString() {
        StringBuilder text = new StringBuilder();
        text.append("firstName=" + firstName);
        text.append(", lastName=" + lastName);
        return text.toString();
    }
}
