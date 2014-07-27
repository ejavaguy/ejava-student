package myorg.relex;

import java.io.Serializable;



import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity @Table(name="EM_AUTO")
public class Auto implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;
    private String make;
    private String model;
    private String color;
    private int mileage;
        
    public long getId() {
        return id;
    }
    @SuppressWarnings("unused")
    private void setId(long id) {
        this.id = id;
    }
    public String getMake() {
        return make;
    }
    public void setMake(String make) {
        this.make = make;
    }
    public int getMileage() {
        return mileage;
    }
    public void setMileage(int mileage) {
        this.mileage = mileage;
    }
    public String getModel() {
        return model;
    }
    public void setModel(String model) {
        this.model = model;
    }
    public String getColor() {
        return color;
    }
    public void setColor(String color) {
        this.color = color;
    }
    
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder
		    .append("id=").append(id)
			.append(", make=").append(make)
			.append(", model=").append(model)
			.append(", color=").append(color)
			.append(", mileage=").append(mileage);
		return builder.toString();
	}    
}
