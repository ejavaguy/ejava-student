package org.myorg.jpatickets.bo;

import java.io.Serializable;

import javax.persistence.*;

@SuppressWarnings("serial")
@Entity
@Table(name="JPATICKETS_VENUE")
public class Venue implements Serializable {
    @Id
    @Column(name="VENUE_ID", length=3)
    private String id;
    
    @Column(name="NAME", length=20, unique=true)
    private String name;
    
    @Embedded
    private Address address;

    public Venue() {}
    public Venue(String id) { this.id = id; }    
    public String getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) {
        this.name = name;
    }
    public Venue withName(String name) {
        setName(name);
        return this;
    }

    public Address getAddress() { return address; }
    public void setAddress(Address address) {
        this.address = address;
    }
    public Venue withAddress(Address address) {
        setAddress(address);
        return this;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) { return true; }
        if (obj == null) { return false; }
        if (getClass() != obj.getClass()) { return false; }
        Venue other = (Venue) obj;
        
        return (id == null ? other.id == null : id.equals(other.id)) &&
           (name == null ? other.name == null : name.equals(other.name));
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Venue [id=").append(id)
                .append(", name=").append(name)
                .append(", address=").append(address)
                .append("]");
        return builder.toString();
    }
}
