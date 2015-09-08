package ejava.projects.eleague.bo;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * This is an example eLeague Venue class. It will use full JPA annotations
 * to define the mappings to the database. We could have also used an orm.xml
 * file supplied by the DAO.
 * 
 * @author jcstaff
 *
 */
@Entity @Table(name="ELEAGUE_VEN")
public class Venue implements Serializable {
	private static final long serialVersionUID = 1L;
	private long id;
    private String name;
    private Address address;
    
    public Venue() {}
    public Venue(long id) {
        this(null, null);
    }

    public Venue(String name, Address address) {
        this.name = name;
        this.address = address;
    }
    
    @Id @GeneratedValue
    public long getId() {
        return id;
    }
    protected void setId(long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(name="ADDR_ID", insertable=true, nullable=false)
    public Address getAddress() {
        return address;
    }
    public void setAddress(Address address) {
        this.address = address;
    }
    
    public String toString() {
        StringBuilder text = new StringBuilder();
        text.append("id=" + id);
        text.append(", name=" + name);
        text.append(", address=" + address);        
        return text.toString();
    }
    
}
