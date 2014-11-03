package ejava.examples.webtier.bo;

import java.io.Serializable;

import javax.persistence.*;

@MappedSuperclass //bug in hibernate requires use of annotation here vs XML
public class Person implements Serializable {
    private static final long serialVersionUID = 1L;
    private long id;
    private String firstName;
    private String lastName;
    
    public Person() {}
    public Person(long id) { setId(id); }
    public Person(long id, String firstName, String lastName) {
        setId(id);
        setFirstName(firstName);
        setLastName(lastName);
    }
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    public long getId() {
        return id;
    }
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
