package ejava.projects.edmv.bo;


/**
 * This class is used to represent a location from the test database.
 *
 */
public class DMVLocation {
    protected long id;
    protected String streetNumber;
    protected String streetName;
    protected String cityName;
    protected String state;
    protected String zip;
    
    
    protected DMVLocation() {
    }
    public long getId() {
        return id;
    }
    protected void setId(long id) {
        this.id = id;
    }
    
    public String getStreetNumber() {
        return streetNumber;
    }
    public void setStreetNumber(String number) {
        this.streetNumber = number;
    }
    
    public String getStreetName() {
        return streetName;
    }
    public void setStreetName(String name) {
        this.streetName = name;
    }

    public String getCityName() {
        return cityName;
    }
    public void setCityName(String name) {
        this.cityName = name;
    }
    
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }
    public void setZip(String zip) {
        this.zip = zip;
    }    
}
