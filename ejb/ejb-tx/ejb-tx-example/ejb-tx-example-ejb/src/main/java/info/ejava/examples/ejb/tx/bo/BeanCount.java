package info.ejava.examples.ejb.tx.bo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="EJBTX_BEANCOUNT")
public class BeanCount {
    @Id 
    @Column(name="BEAN_NAME", length=32)
    String name;
    
    @Column(name="COUNT")
    int count;
    
    protected BeanCount() {}
    public BeanCount(String beanName) {
        this.name = beanName;
    }
    public String getName() { return name; }
    
    public int getCount() { return count; }
    public void setCount(int count) {
        this.count = count;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("BeanCount [name=").append(name)
                .append(", count=").append(count).append("]");
        return builder.toString();
    }
}
