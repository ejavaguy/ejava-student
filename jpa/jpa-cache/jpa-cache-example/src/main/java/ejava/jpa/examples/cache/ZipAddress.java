package ejava.jpa.examples.cache;

import javax.persistence.*;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name="JPACACHE_ZIPADDR")
@Cacheable(true)
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ZipAddress {
	@Id
	@Column(length=10)
	private String zip;
	@Column(length=32, nullable=false)
	private String city;
	
	protected ZipAddress() {}
	public ZipAddress(String zip, String city) {
		this.zip=zip;
		this.city=city;
	}
	
	public ZipAddress(String zip) {
		this.zip=zip;
	}
	public String getZip() { return zip; }
	public String getCity() { return city; }
	
	@Override
	public String toString() {
		return zip + "=" + city;
	}
	
	@Override
	public int hashCode() {
		return zip==null? 0 : zip.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj==null) { return false; }
		if (obj==this) { return true; }
		if (!(obj instanceof ZipAddress)) { return false; }
		ZipAddress rhs = (ZipAddress) obj;
		return zip==null ? rhs.zip==null : zip.equals(rhs.zip);
	}
}
