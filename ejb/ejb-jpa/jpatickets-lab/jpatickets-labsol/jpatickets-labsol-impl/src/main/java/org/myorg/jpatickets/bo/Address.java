package org.myorg.jpatickets.bo;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Address {
    @Column(name="STREET", length=20)
    private String street;
    @Column(name="CITY", length=20)
    private String city;
    @Column(name="STATE", length=2)
    private String state;
    @Column(name="POSTAL_CODE")
    private int zipCode;
    
    public String getStreet() { return street; }
    public void setStreet(String street) {
        this.street = street;
    }
    public Address withStreet(String street) {
        setStreet(street);
        return this;
    }
    
    public String getCity() { return city; }
    public void setCity(String city) {
        this.city = city;
    }
    public Address withCity(String city) {
        setCity(city);
        return this;
    }
    
    public String getState() { return state; }
    public void setState(String state) {
        this.state = state;
    }
    public Address withState(String state) {
        setState(state);
        return this;
    }

    public int getZipCode() { return zipCode; }
    public void setZipCode(int zipCode) {
        this.zipCode = zipCode;
    }
    public Address withZipCode(int zipCode) {
        setZipCode(zipCode);
        return this;
    }
    
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((city == null) ? 0 : city.hashCode());
        result = prime * result + ((state == null) ? 0 : state.hashCode());
        result = prime * result + ((street == null) ? 0 : street.hashCode());
        result = prime * result + zipCode;
        return result;
    }
    
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) { return true; }
        if (obj == null) { return false; }
        if (getClass() != obj.getClass()) { return false; }
        Address other = (Address) obj;
        
        return (city == null ? other.city == null : city.equals(other.city)) &&
            (state == null ? other.state == null : state.equals(other.state)) &&
            (street == null ? other.street == null : street.equals(other.street)) &&
            (zipCode == other.zipCode);
    }
    
    
}
