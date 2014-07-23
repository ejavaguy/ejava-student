package myorg.entityex.mapped;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Cat {
	private static final Log log = LogFactory.getLog(Cat.class);
	private int id;
	private String name;
	private Date dob;
	private double weight;
	
	public Cat() {} //must have default ctor
	public Cat(String name, Date dob, double weight) {
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
	
	public BigDecimal getWeight() {
		log.debug("mapped.getWeight()");
		return new BigDecimal(weight); 
	}
	public void setWeight(BigDecimal weight) {
		log.debug("mapped.setWeight()");
		this.weight = weight==null ? 0 : weight.doubleValue();
	}
}
