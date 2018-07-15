package ejava.projects.edmv.bo;

import java.io.Serializable;

import javax.persistence.*;

/**
 * This class provides a sparse _example_ of a person entity that will get
 * populated from the ingested data and inserted into the DB. 
 * 
 * @author jcstaff
 *
 */
@Entity(name="Person")
@Table(name="EDMV_PERSON")
@SuppressWarnings("serial")
public class Person implements Serializable {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;
    
    @Column(name="LAST_NAME", length=20)
    private String lastName;    

    //JPA requires a no-arg ctor
    public Person() {}
    public Person(long id) {
        this.id = id;
    }
    
    public long getId() {
        return id;
    }
    //this is non-public to implement read-only behavior
    @SuppressWarnings("unused")
    private void setId(long id) {
        this.id = id;
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
        text.append(", lastName=" + lastName);
        
        return text.toString();
    }
}
