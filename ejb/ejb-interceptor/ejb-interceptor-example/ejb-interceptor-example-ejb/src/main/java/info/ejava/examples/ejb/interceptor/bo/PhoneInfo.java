package info.ejava.examples.ejb.interceptor.bo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@SuppressWarnings("serial")
@Entity
@Table(name="EJBINTERCEPTOR_PHONEINFO")
public class PhoneInfo extends ContactInfo {
    @NotNull(groups={PreNormalizedCheck.class, PrePersistCheck.class})
    @Size(min=12, max=12, groups=PostNormalizedCheck.class)
    @Pattern.List({
        @Pattern(regexp="^[0-9-]+$", groups=PreNormalizedCheck.class),
        @Pattern(regexp="^[0-9]{3}-[0-9]{3}-[0-9]{4}$", groups=PostNormalizedCheck.class)
    })
    @Column(name="PHONE_NUMBER", nullable=false, length=12)
    private String phoneNumber;
    
    @Column(name="EXTENSION")
    private Integer extension;

    @Override
    public ContactType getContactType() {
        return ContactType.PHONE;
    }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Integer getExtension() { return extension; }
    public void setExtension(Integer extension) {
        this.extension = extension;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PhoneInfo [")
                .append("getRole()=").append(getRole())
                .append(", number=").append(phoneNumber)
                .append(", ext=").append(extension)
                .append("]");
        return builder.toString();
    }
}
