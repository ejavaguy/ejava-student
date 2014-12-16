package info.ejava.examples.ejb.interceptor.bo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@SuppressWarnings("serial")
@Embeddable
public class PostalAddress implements Serializable {
    @NotNull(groups={PreNormalizedCheck.class, PrePersistCheck.class})
    @Size(min=2, max=30, groups={PostNormalizedCheck.class, PrePersistCheck.class})
    @Pattern.List({
        @Pattern(regexp="^[0-9-A-Za-z'\\ ]+$", groups={PreNormalizedCheck.class}),
        @Pattern(regexp="^([0-9]+\\ )([A-Z][a-z0-9-]+\\ *)+$", groups=PostNormalizedCheck.class)
    })
    @Column(name="STREET1", length=30, nullable=false)
    private String street1;
    
    @Pattern.List({
        @Pattern(regexp="^[0-9-A-Za-z'\\ ]+$", groups={PreNormalizedCheck.class}),
        @Pattern(regexp="^([A-Z][a-z0-9-]+\\ *)+$", groups=PostNormalizedCheck.class)
    })
    @Column(name="STREET2", length=30)
    private String street2;    
    
    @NotNull(groups={PreNormalizedCheck.class, PrePersistCheck.class})
    @Pattern.List({
        @Pattern(regexp="^[A-Za-z-'\\ ]+$", groups={PreNormalizedCheck.class}),
        @Pattern(regexp="^([A-Z][a-z-]+\\ *)+$", groups=PostNormalizedCheck.class)
    })
    private String city;
    
    @NotNull(groups=PrePersistCheck.class)
    @Size(min=2, max=2, groups={PostNormalizedCheck.class, PrePersistCheck.class})
    @Pattern.List({
        @Pattern(regexp="[A-Za-z]{2}", groups=PreNormalizedCheck.class),
        @Pattern(regexp="[A-Z][A-Z]", groups=PostNormalizedCheck.class)
    })
    private String state;
    
    @NotNull(groups={PreNormalizedCheck.class, PrePersistCheck.class})
    @Size(min=5, max=10, groups={PostNormalizedCheck.class, PrePersistCheck.class})
    @Pattern(regexp="^[0-9]{5}(-[0-9]{4})?$", groups=PostNormalizedCheck.class)
    private String zip;

    public String getStreet1() { return street1; }
    public void setStreet1(String street1) {
        this.street1 = street1;
    }
    
    public String getStreet2() { return street2; }
    public void setStreet2(String street2) {
        this.street2 = street2;
    }
    
    public String getCity() { return city; }
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getState() { return state; }
    public void setState(String state) {
        this.state = state;
    }
    
    public String getZip() { return zip; }
    public void setZip(String zip) {
        this.zip = zip;
    }
    
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("street1=").append(street1)
                .append(", street2=").append(street2)
                .append(", city=").append(city)
                .append(", state=").append(state)
                .append(", zip=").append(zip);
        return builder.toString();
    }
}
