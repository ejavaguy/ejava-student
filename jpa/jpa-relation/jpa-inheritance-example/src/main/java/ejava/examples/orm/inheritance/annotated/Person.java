package ejava.examples.orm.inheritance.annotated;

import javax.persistence.*;

/**
 * This class provides an example base class in a join inheritance strategy.
 * In thise mode, the base and all derived classes declare their own table
 * and all are joined using a common primary key.
 */

@Entity @Table(name="ORMINH_PERSON")
@Inheritance(strategy=InheritanceType.JOINED)
public class Person {
    @Id @GeneratedValue
    private long id;
    private String firstName;
    private String lastName;
    
    public Person() {}
    public Person(long id) { this.id=id; }
    public long getId() { return id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String toString() {
        StringBuilder text = new StringBuilder(super.toString());
        text.append(", id=" + id);
        text.append(", firstName=" + firstName);
        text.append(", lastName=" + lastName);
        return text.toString();
    }
}
