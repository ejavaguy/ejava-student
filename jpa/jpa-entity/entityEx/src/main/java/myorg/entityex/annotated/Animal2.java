package myorg.entityex.annotated;

import java.util.Date;

@javax.persistence.Entity
public class Animal2 {
	@javax.persistence.Id
	private int id;
	private String name;
	private Date dob;
	private double weight;
	
	public Animal2() {} //must have default ctor
	public Animal2(String name, Date dob, double weight) {
		this.name = name;
		this.dob = dob;
		this.weight = weight;
	}
	
	public int getId() { return id; }
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() { return name; }
	public void setName(String name) {
		this.name = name;
	}
	
	public Date getDob() { return dob; }
	public void setDob(Date dob) {
		this.dob = dob;
	}
	
	public double getWeight() { return weight; }
	public void setWeight(double weight) {
		this.weight = weight;
	}
}
