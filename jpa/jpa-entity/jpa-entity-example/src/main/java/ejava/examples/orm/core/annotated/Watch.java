package ejava.examples.orm.core.annotated;

import javax.persistence.*;

/**
 * This class provides an example of joining three tables to make a single
 * object. The WATCH, OWNER, and MAKER tables are joined by a common 
 * primary key value. Of course, this requires a one-to-one mapping of 
 * all 3 tables. If not, then we need to move to relationship mappings.
 */
@Entity
@Table(name="ORMCORE_WATCH")
@SecondaryTables({
    @SecondaryTable(name="ORMCORE_OWNER",
        pkJoinColumns={
            @PrimaryKeyJoinColumn(name="OWNER_ID")}),
    @SecondaryTable(name="ORMCORE_MAKER",
        pkJoinColumns={
            @PrimaryKeyJoinColumn(name="MAKER_ID")})
})
public class Watch {
    @Id
    private long id;
    private String make;
    private String model;
    @Column(name="NAME", table="ORMCORE_OWNER")
    private String owner;
    @Column(table="ORMCORE_OWNER")
    private String cardnum;
    @Column(name="NAME", table="ORMCORE_MAKER")
    private String manufacturer;
    @Column(table="ORMCORE_MAKER")
    private String address;
    @Column(table="ORMCORE_MAKER")
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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(super.toString())
               .append(", id=").append(id)
               .append(", make=").append(make)
                   .append(", model=").append(model)
                   .append(", owner=").append(owner)
                   .append(", cardnum=").append(cardnum)
                   .append(", manufacturer=").append(manufacturer)
                   .append(", address=").append(address)
                   .append(", phone=").append(phone);
        return builder.toString();
    }
}
