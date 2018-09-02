package ejava.projects.edmv.bo;

import java.util.Date;

/**
 * This class is used to represent a residence in the test data database.
 *
 */
public class DMVResidence {
    protected long id;
    protected Date startDate;
    protected Date endDate;
    protected DMVLocation location;
    //protected DMVPerson person;
    
    public DMVResidence() {
    }
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    
    
    public Date getStartDate() {
        return startDate;
    }
    public void setStartDate(Date startDate) throws Exception {
        this.startDate = startDate;
    }
    
    public Date getEndDate() {
        return endDate;
    }
    public void setEndDate(Date endDate) throws Exception {
        this.endDate = endDate;
    }
    
    public DMVLocation getLocation() {
        return location;
    }
    public void setLocation(DMVLocation location) {
        this.location = location;
    }
    /*
    protected DMVPerson getPerson() {
        return person;
    }
    protected void setPerson(DMVPerson person) {
        this.person = person;
    }
    */
}
