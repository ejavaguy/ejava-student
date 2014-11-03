package ejava.examples.daoex.bo;

import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.Column;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@javax.persistence.Entity(name="jpaAuthor")
@Table(name="DAO_AUTHOR")
@SequenceGenerator(name="AUTHOR_SEQUENCE", sequenceName="AUTHOR_SEQ")
public class Author {
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="AUTHOR_SEQUENCE")
    @Column(name="AUTHOR_ID", nullable=false)
    private long id;
    
    @Column(name="VERSION", nullable=false)
    private long version=0;

    @Column(name="FIRST_NAME", length=32)
    private String firstName;

    @Column(name="LAST_NAME", length=32)
    private String lastName;

    @Column(name="SUBJECT", length=32)
    private String subject;

    @Column(name="PUBLISH_DATE")
    private Date publishDate;
    
    public Author() {}        
    public Author(long id) {
        this.id = id;
    }    
    
    public long getId() {
        return id;
    }
    @SuppressWarnings("unused")
    private void setId(long id) {
        this.id = id;
    }    
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }    
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }    
    public Date getPublishDate() {
        return publishDate;
    }
    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }    
    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }    
    public long getVersion() {
        return version;
    }
    public void setVersion(long version) {
        this.version = version;
    }
    
    public String toString() {
        StringBuilder text = new StringBuilder();
        text.append(super.toString());
        text.append(", id=" + id);
        text.append(", fn=" + firstName);
        text.append(", ln=" + lastName);
        text.append(", subject=" + subject);
        text.append(", pdate=" + publishDate);
        text.append(", version=" + version);
        return text.toString();
    }
}
