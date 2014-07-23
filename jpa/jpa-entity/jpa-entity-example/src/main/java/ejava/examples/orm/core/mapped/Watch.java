package ejava.examples.orm.core.mapped;

/**
 * This class provides an example of joining three tables to make a single
 * object. The WATCH, OWNER, and MAKER tables are joined by a common 
 * primary key value. Of course, this requires a one-to-one mapping of 
 * all 3 tables. If not, then we need to move to relationship mappings.
 */
public class Watch {
    private long id;
    private String make;
    private String model;
    private String owner;
    private String cardnum;
    private String manufacturer;
    private String address;
    private String phone;

    public Watch() {}
    public Watch(long id) { this.id = id; }
    
    public long getId() { return id; }

    public String getMake() { return make; }
    public void setMake(String make) {
        this.make = make;
    }
    public String getModel() { return model; }
    public void setModel(String model) {
        this.model = model;
    }

    public String getOwner() { return owner; }
    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getCardnum() { return cardnum; }
    public void setCardnum(String cardnum) {
        this.cardnum = cardnum;
    }
    
    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getAddress() { return address; }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() { return phone; }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String toString() {
        return super.toString() +
            ", id=" + id +
            ", make=" + make + 
            ", model=" + model +
            ", owner=" + owner +
            ", cardnum=" + cardnum +
            ", manufacturer=" + manufacturer +
            ", address=" + address +
            ", phone=" + phone;
    }
}
