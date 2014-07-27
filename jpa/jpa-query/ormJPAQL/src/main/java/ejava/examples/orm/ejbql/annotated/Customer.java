package ejava.examples.orm.ejbql.annotated;

import javax.persistence.*;

@Entity @Table(name="ORMQL_CUSTOMER")
@NamedQueries({
    @NamedQuery(name="getCustomersByName",
            query="select c from Customer c " +
                    "where c.firstName like :first AND c.lastName like :last"),
    @NamedQuery(name="getCustomerPurchases",
            query="select s from Sale s " +
                    "where s.buyerId=:custId")
})
public class Customer {
    private long id;
    private String firstName;
    private String lastName;
    
    @Id @GeneratedValue @Column(name="CUSTOMER_ID")
    public long getId() {
        return id;
    }
    @SuppressWarnings("unused")
    private void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String toString() {
        StringBuilder text = new StringBuilder();
        text.append("id=" + id);
        text.append(", firstName=" + firstName);
        text.append(", lastName=" + lastName);
        return text.toString();
    }

}
