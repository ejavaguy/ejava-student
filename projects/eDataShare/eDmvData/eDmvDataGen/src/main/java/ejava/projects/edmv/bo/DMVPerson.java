package ejava.projects.edmv.bo;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to represent a person from the test database.
 * 
 * @author jcstaff
 *
 */
public class DMVPerson {
    protected long id;
    protected String givenName;
    protected String middleName;
    protected String surName;
    protected String suffixName;
    protected DMVPersonPhysicalDetails physicalDetails;
    List<DMVResidence> residences = new ArrayList<DMVResidence>();
    
    public DMVPerson() {
    }
    
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getGivenName() {        
        return givenName;
    }
    public void setGivenName(String name) {
        this.givenName = name;
    }
    public String getMiddleName() {        
        return middleName;
    }
    public void setMiddleName(String name) {
        this.middleName = name;
    }
    public String getSurName() {        
        return surName;
    }
    public void setSurName(String name) {
        this.surName = name;
    }
    public String getSuffixName() {        
        return suffixName;
    }
    public void setSuffixName(String name) {
        this.suffixName = name;
    }
    

    public DMVPersonPhysicalDetails getPhysicalDetails() {
        return physicalDetails;
    }
    public void setPhysicalDetails(
            DMVPersonPhysicalDetails physicalDetails) {
        this.physicalDetails = physicalDetails;
    }
    
    
    public List<DMVResidence> getResidences() {
        return residences;
    }
    public void setResidences(List<DMVResidence> residences) {
        this.residences = residences; 
    }

}
