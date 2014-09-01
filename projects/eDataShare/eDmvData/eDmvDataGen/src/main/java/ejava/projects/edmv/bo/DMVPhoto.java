package ejava.projects.edmv.bo;

/**
 * This class is used to represent a photo in the test data database.
 * 
 * @author jcstaff
 *
 */
public class DMVPhoto {
    protected long id;
    protected byte[] image;
    
    protected DMVPhoto() {};
    protected DMVPhoto(int id) {
        this.id = id;
    }
    protected DMVPhoto(int id, byte[] image) {
        this.id = id;
        this.image = image;
    }
    
    public long getId() {
        return id;
    }
    protected void setId(long id) {
        this.id = id;
    }
    public byte[] getImage() {
        return image;
    }
    public void setImage(byte[] image) {
        this.image = image;
    }
    
}
