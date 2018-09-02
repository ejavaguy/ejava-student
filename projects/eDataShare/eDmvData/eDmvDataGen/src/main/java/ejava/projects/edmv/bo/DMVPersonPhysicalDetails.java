package ejava.projects.edmv.bo;

import java.util.Date;

/**
 * This class is used to represent the physical details of a person from the
 * test data database.
 *
 */
public class DMVPersonPhysicalDetails {
    protected long id;
    protected int height;
    protected int weight;
    protected String hairColor;
    protected String eyeColor;
    protected String sex;
    protected Date dob;
    protected DMVPhoto photo;
    
    protected DMVPersonPhysicalDetails() {
    }
    public DMVPersonPhysicalDetails(long id) {
        this.id=id;
    }
    public long getId() { 
        return id;
    }
    protected void setId(long id) {
        this.id = id;
    }
    
    
    public Date getDob() {
        return dob;
    }
    public void setDob(Date dob) {
        this.dob = dob;
    }
    
    
    public int getHeight() {
        return height;
    }
    public void setHeight(int height) {
        this.height = height;
    }    
    public int getWeight() {
        return weight;
    }
    public void setWeight(int weight) {
        this.weight = weight;
    }    
    public String getHairColor() {
        return hairColor;
    }
    public void setHairColor(String hairColor) {
        this.hairColor = hairColor;
    }
    public String getEyeColor() {
        return eyeColor;
    }
    public void setEyeColor(String eyeColor) {
        this.eyeColor = eyeColor;
    }
    public String getSex() {
        return sex;
    }
    public void setSex(String sex) {
        this.sex = sex;
    }
    public DMVPhoto getPhoto() {
        return photo;
    }
    public void setPhoto(DMVPhoto photo) {
        this.photo = photo;
    }
}
