package info.ejava.examples.ejb.ejbjpa.bo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="EJBJPA_GUEST")
public class Guest implements Serializable {
    @Id @GeneratedValue
    @Column(name="GUEST_ID")
    private int id;
    
    @Column(name="name", length=60, nullable=false)
    private String name;
    
    public Guest() {}
    public Guest(int id) {
        this.id = id;
    }
    public Guest(String name) {
        this.name=name;
    }
    
    public int getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Guest [id=").append(id)
            .append(", name=").append(name)
            .append("]");
        return builder.toString();
    }
}
