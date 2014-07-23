package ejava.examples.orm.core.annotated;

import javax.persistence.*;

/**
 * This class provides an example of an object with no identity of its own
 * and must be stored within a containing object. See the XRay class for
 * an example of a containing object.
 */
@Embeddable
public class Manufacturer {
    private String name;
    private String address;
    private String phone;
    
    public Manufacturer() {}
    public Manufacturer(String name, String address, String phone) {
        this.name = name;
        this.address = address;
        this.phone=phone;
    }
    
    public String getAddress() { return address; }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() { return name; }
    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() { return phone; }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String toString() {
        return new StringBuilder()
           .append(super.getClass().getName())
           .append(", name=").append(name)
           .append(", address=").append(address)
           .append(", phone=").append(phone)
           .toString();        
    }
}
