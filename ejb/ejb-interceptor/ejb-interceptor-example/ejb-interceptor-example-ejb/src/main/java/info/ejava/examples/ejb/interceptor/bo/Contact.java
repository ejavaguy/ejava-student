package info.ejava.examples.ejb.interceptor.bo;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
@Entity
@Table(name="EJBINTERCEPTOR_CONTACT")
@NamedQueries({
    @NamedQuery(name="EJBInterceptorContact.getContact", 
        query="select distinct c from Contact c "
                + "left join fetch c.contactInfo "
                + "where c.normalizedName like :name")
})
public class Contact implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(Contact.class);
    
    @Id @GeneratedValue
    @Column(name="CONTACT_ID")
    private int id;
    
    @NotNull(groups={PreNormalizedCheck.class, PrePersistCheck.class})
    @Size(min=1, max=32, groups={PostNormalizedCheck.class, PrePersistCheck.class})
    @Pattern.List({
        @Pattern(regexp="^[A-Za-z0-9-\\ ]+$", groups=PreNormalizedCheck.class),
        @Pattern(regexp="^([A-Z][a-z0-9-]+\\ *)+$", groups=PostNormalizedCheck.class)
    })
    @Column(name="CONTACT_NAME", length=32, nullable=false)
    private String name;
    
    @Column(name="NORMALIZED_NAME", length=32, nullable=false)
    private String normalizedName;
    
    @PrePersist
    @PreUpdate
    private void normalizeName() {
        normalizedName = (name == null) ? null : name.toLowerCase().trim();
        logger.debug(this.toString());
    }
    
    @Valid
    @OneToMany(fetch=FetchType.LAZY, mappedBy="contact", orphanRemoval=true, 
        cascade={CascadeType.PERSIST, CascadeType.REMOVE})
    private List<ContactInfo> contactInfo;

    
    public int getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) {
        this.name = name;
    }
    public Contact withName(String name) {
        setName(name);
        return this;
    }
    
    public List<ContactInfo> getContactInfo() {
        return contactInfo;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Contact [id=").append(id)
            .append(", name=").append(name)
            .append(", normalizedName=").append(normalizedName)
            .append("]");
        return builder.toString();
    }
    
    
}
