package ejava.examples.orm.rel.annotated;

import javax.persistence.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class provides an example of the owning side of a OneToOne 
 * Uni-directional relationship. The person has been physically separated
 * into two tables and objects. This class maintains the core properties.
 * The photo object maintains the larger and optional photo. 
 */
@Entity
@Table(name="ORMREL_PERSON")
public class Person  {
    private static Log log = LogFactory.getLog(Person.class);
    @Id @GeneratedValue @Column(name="PERSON_ID")
    private long id;
    private String firstName;
    private String lastName;
    private String phone;

    @OneToOne(cascade={ 
            CascadeType.ALL},  //have creates, deletes, etc. cascade to photo
            fetch=FetchType.LAZY)       //a hint that we don't need this
    @JoinColumn(name="PERSON_PHOTO")    //define local foreign key column
    private Photo photo;

    public Person() { log.info(super.toString() + ", ctor()");}

    public long getId() {
        return id;
    }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() { return phone; }
    public void setPhone(String phone) {
        this.phone = phone;
    }    

    public Photo getPhoto() { return photo; }
    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    public String toString() {
        return super.toString() +
            ", id=" + id +
            ", name=" + firstName + " " + lastName +
            ", phone=" + phone +
            ", photo=" + photo;
    }

}
