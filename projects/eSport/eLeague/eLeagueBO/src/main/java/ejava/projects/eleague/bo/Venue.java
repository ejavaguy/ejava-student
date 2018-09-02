package ejava.projects.eleague.bo;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * This is an example eLeague Venue class. It will use full JPA annotations
 * to define the mappings to the database. We could have also used an orm.xml
 * file supplied by the DAO.
 */
@Entity @Table(name="ELEAGUE_VEN")
public class Venue implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	
	@Column(length=40)
    private String name;

    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(name="ADDR_ID", insertable=true, nullable=false)
    private Address address;
    
    public Venue() {}
    public Venue(long id) {
        this(null, null);
    }

    public Venue(String name, Address address) {
        this.name = name;
        this.address = address;
    }
    
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
    
    public Address getAddress() {
        return address;
    }
    public void setAddress(Address address) {
        this.address = address;
    }
    
    public String toString() {
        StringBuilder text = new StringBuilder();
        text.append("id=").append(id);
        text.append(", name=").append(name);
        text.append(", address=").append(address);        
        return text.toString();
    }
    
}
