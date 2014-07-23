package myorg.entityex.annotated;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@javax.persistence.Entity
@javax.persistence.Table(name="ENTITYEX_CAT")
@javax.persistence.Access(javax.persistence.AccessType.FIELD)
public class Cat2 {
	private static final Log log = LogFactory.getLog(Cat2.class);
	@javax.persistence.Id
	@javax.persistence.Column(name="CAT_ID")
	@javax.persistence.GeneratedValue
	private int id;
	@javax.persistence.Column(nullable=false, length=20)
	private String name;
	@javax.persistence.Temporal(javax.persistence.TemporalType.DATE)
	private Date dob;
	private double weight;
	
	public Cat2() {} //must have default ctor
	public Cat2(String name, Date dob, double weight) {
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
	
	@javax.persistence.Column(precision=3, scale=1)  //10.2lbs
	@javax.persistence.Access(javax.persistence.AccessType.PROPERTY)
	public BigDecimal getWeight() {
		log.debug("annotated.getWeight()");
		return new BigDecimal(weight); }
	public void setWeight(BigDecimal weight) {
		log.debug("annotated.setWeight()");
		this.weight = weight==null ? 0 : weight.doubleValue();
	}
}
