package ejava.jpa.examples.tuning.bo;

import java.io.Serializable;
import java.util.Date;

public class MoviePK implements Serializable {
	private static final long serialVersionUID = -4411388426942024278L;
	private String title;
	private Date releaseDate;

	protected MoviePK() {}
	public MoviePK(String title, Date releaseDate) {
		this.title = title;
		this.releaseDate = releaseDate;
	}
	
	public String getTitle() { return title; }
	public Date getReleaseDate() { return releaseDate; }
	
	@Override
	public int hashCode() {
		return (title==null?0:title.hashCode()) +
				(releaseDate==null?0:releaseDate.hashCode());
	}
	
	@Override
	public boolean equals(Object obj) {
		try {
			if (this == obj) { return true; }
			if (obj == null) { return false; }
			MoviePK rhs = (MoviePK)obj;
			if (title != null && releaseDate != null) {
				return title.equals(rhs.title) && releaseDate.equals(rhs.releaseDate);
			}
			if (title==null && rhs.title != null) { return false; }
			if (releaseDate==null && rhs.releaseDate != null) { return false; }
			return true;
		} catch (Exception ex) { return false; }
	}
	
}
