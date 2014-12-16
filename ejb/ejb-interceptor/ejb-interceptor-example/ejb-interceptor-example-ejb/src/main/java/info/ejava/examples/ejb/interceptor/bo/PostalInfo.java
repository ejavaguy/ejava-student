package info.ejava.examples.ejb.interceptor.bo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;


@SuppressWarnings("serial")
@Entity
@Table(name="JPAINTERCEPTOR_PADDRESS")
public class PostalInfo extends ContactInfo{
    @NotNull(groups={PreNormalizedCheck.class, PrePersistCheck.class})
    @Enumerated(EnumType.STRING)
    @Column(name="CONTACT_TYPE", length=10)
    private AddressType type;
    
    @NotNull(groups={PreNormalizedCheck.class})
    @Valid
    private PostalAddress address;
    
    @Override
    public ContactType getContactType() {
        return ContactType.ADDRESS;
    }

    public AddressType getType() { return type; }
    public void setType(AddressType type) {
        this.type = type;
    }

    public PostalAddress getAddress() { return address; }
    public void setAddress(PostalAddress address) {
        this.address = address;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PostalInfo [")
            .append("role=").append(getRole())
            .append(", type=").append(type)
            .append(", address=")
            .append(address)
            .append("]");
        return builder.toString();
    }
}
