package ejava.projects.edmv.bo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

/**
 * This class provides a sparse _example_ implementation of a vehicle
 * entity that will get populated from the ingested data from the parser.
 * 
 */
@Entity(name="VehicleRegistration")
@Table(name="EDMV_VREG")
@SuppressWarnings("serial")
public class VehicleRegistration implements Serializable {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;
    
    @Column(length=20)
    private String vin;

    @ManyToMany()
    @JoinTable(name="EDMV_VREG_OWNER_LINK",
            joinColumns={@JoinColumn(name="VEHICLE_ID")},
            inverseJoinColumns={@JoinColumn(name="OWNER_ID")}
    )
    private List<Person> owners = new ArrayList<Person>();
   
    //jpa requires a no-arg ctor
    public VehicleRegistration() {}
    public VehicleRegistration(long id) {
        this.id = id;
    }
   
    public long getId() {
        return id;
    }
    //hide setter to implement read-only functionality
    @SuppressWarnings("unused")
    private void setId(long id) {
        this.id = id;
    }
    
    public String getVin() {
        return vin;
    }
    public void setVin(String vin) {
        this.vin = vin;
    }
    
    public List<Person> getOwners() {
        return owners;
    }
    public void setOwners(List<Person> owners) {
        this.owners = owners;
    }
   
    public String toString() {
        StringBuilder text = new StringBuilder();
        
        text.append("id=" + id);
        text.append(", vin=" + vin);
        text.append(", owners={");
        for (Person p : owners) {
            text.append(p + ",");    
        }
        text.append("}");
        
        return text.toString();
    }
}
