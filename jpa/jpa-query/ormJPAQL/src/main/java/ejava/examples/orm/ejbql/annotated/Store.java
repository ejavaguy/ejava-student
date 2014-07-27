package ejava.examples.orm.ejbql.annotated;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.*;

@Entity @Table(name="ORMQL_STORE")
public class Store {
    private long id;
    private String name;
    private Collection<Sale> sales = new ArrayList<Sale>();

    @Id @GeneratedValue @Column(name="STORE_ID")
    public long getId() {
        return id;
    }
    @SuppressWarnings("unused")
    private void setId(long id) {
        this.id = id;
    }

    @OneToMany(mappedBy="store", 
               cascade={CascadeType.REMOVE}, 
               fetch=FetchType.LAZY)
    public Collection<Sale> getSales() {
        return sales;
    }
    public void setSales(Collection<Sale> sales) {
        this.sales = sales;
    }    
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    public String toString() {
        StringBuilder text = new StringBuilder();
        text.append("id=" + id);
        text.append(", name=" + name);
        text.append(", sales(" + sales.size() + ")={");
        for(Sale s : sales) {
            text.append(s.getId() + ", ");
        }
        text.append("}");
        return text.toString();
    }
}
