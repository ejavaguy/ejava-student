package ejava.examples.schemagen;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="JPAUTIL_TABLET")
public class Tablet {
	@Id @GeneratedValue
    private int id;
    private String maker;
    
    public Tablet() {}
    public Tablet(int id) {
    		this.id = id;
    }

	public int getId() {
		return id;
	}

	public String getMaker() {
		return maker;
	}
	public void setMaker(String maker) {
		this.maker = maker;
	}
}
