package ejava.examples.blpurchase.bo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="BLPURCHASE_PRODUCT")
public class Product {
	@Id @GeneratedValue
	private int id;	
	@Column(nullable=false)
	private String name;
	@Column(nullable=false)
	private double price;
	@Column(nullable=false)
	private int count;

	public Product(){}
	public Product(String name, double price, int count) {
		this.name = name;
		this.price = price;
		this.count=count;
	}
	public int getId() {
		return id;
	}
	protected void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
}
