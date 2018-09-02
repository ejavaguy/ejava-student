package ejava.projects.edmv.bo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class is used to represent a vehicle registration from the test data
 * database.
 *
 */
public class DMVVehicleRegistration {
    protected long id;
    protected String vin;
    protected String make;
    protected String model;
    protected String year;
    protected String color;
    protected String tagNo;
    protected Date expiration;
    protected List<DMVPerson> owners = new ArrayList<DMVPerson>();
    
    
    protected DMVVehicleRegistration() {}
    public DMVVehicleRegistration(long id) {
        this.id = id;
    }
    public long getId() {
        return id;
    }
    protected void setId(long id) {
        this.id = id;
    }
    public String getVin() {
        return vin;
    }
    public void setVin(String vin) {
        this.vin = vin;
    }
    public String getMake() {
        return make;
    }
    public void setMake(String make) {
        this.make = make;
    }
    public String getModel() {
        return model;
    }
    public void setModel(String model) {
        this.model = model;
    }
    public String getYear() {
        return year;
    }
    public void setYear(String year) {
        this.year = year;
    }
    public String getColor() {
        return color;
    }
    public void setColor(String color) {
        this.color = color;
    }
    public String getTagNo() {
        return tagNo;
    }
    public void setTagNo(String tagNo) {
        this.tagNo = tagNo;
    }
    public Date getExpiration() {
        return expiration;
    }
    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }
    public List<DMVPerson> getOwners() {
        return owners;
    }
    public void setOwners(List<DMVPerson> owners) {
        this.owners = owners;
    }
}
