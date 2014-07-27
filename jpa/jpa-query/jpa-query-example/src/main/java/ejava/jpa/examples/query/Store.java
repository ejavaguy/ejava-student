package ejava.jpa.examples.query;

import java.util.ArrayList;

import java.util.List;

import javax.persistence.*;

@Entity @Table(name="JPAQL_STORE")
public class Store {
    @Id @GeneratedValue 
    @Column(name="STORE_ID")
    private long id;
    
    @Column(length=20, nullable=false)
    private String name;

    @OneToMany(mappedBy="store", 
            cascade={CascadeType.REMOVE}, 
            fetch=FetchType.LAZY)
    private List<Sale> sales = new ArrayList<Sale>();

    public long getId() { return id; }
    
    public List<Sale> getSales() { return sales; }
    public Store setSales(List<Sale> sales) {
        this.sales = sales;
        return this;
    }
    public Store addSale(Sale...sale) {
    	if (sale != null) {
    		for (Sale s : sale) {
    			if (s != null) { sales.add(s); }
    		}
    	}
    	return this;
    }
    
    public String getName() { return name; }
    public Store setName(String name) {
        this.name = name;
        return this;
    }
    
    public String toString() {
        StringBuilder text = new StringBuilder();
        text.append("name=" + name);
        text.append(", sales(" + sales.size() + ")={");
        for(Sale s : sales) {
            text.append(s.getId() + ", ");
        }
        text.append("}");
        return text.toString();
    }
}
