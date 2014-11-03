package ejava.examples.orm.core.annotated;

import java.math.BigDecimal;

import javax.persistence.*;

/**
 * This class provides an example of providing more explicite mappings between
 * the entity class and the database using annotations.
 */
@Entity
@Table(name="ORMCORE_CAR")
//we use the @Table name property to specifically name the table in DB
//we can also specify vendor-specific constraints with uniqueConstraints prop
public class Car {    
    @Id
    @Column(name="CAR_ID", nullable=false)
    private long id;

    @Column(name="CAR_MAKE", 
            unique=false, 
            nullable=false, 
            insertable=true,
            updatable=true,
            //table="",  //note: we can point to another table to get prop
            length=20)
    private String make;

    @Column(name="CAR_MODEL", nullable=false, length=20)
    private String model;
    
    @Column(name="CAR_YEAR", nullable=false)
    private int year;

    @Column(name="CAR_COST", precision=7, scale=2)
    private BigDecimal cost;
    
    public Car() {}
    public Car(long id) { this.id = id; }
    
    public long getId() { return id; }
    
    public String getMake() { return make; }
    public void setMake(String make) {
        this.make = make;
    }
    
    public String getModel() { return model; }
    public void setModel(String model) {
        this.model = model;
    }

    public int getYear() { return year; }
    public void setYear(int year) {
        this.year = year;
    }
    
    public BigDecimal getCost() { return cost; }
    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    @Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(super.toString())
	           .append(", id=").append(id)
		       .append(", make=").append(make)
			   .append(", model=").append(model)
			   .append(", year=").append(year)
			   .append(", cost=$").append(cost);
		return builder.toString();
	}
    
}
