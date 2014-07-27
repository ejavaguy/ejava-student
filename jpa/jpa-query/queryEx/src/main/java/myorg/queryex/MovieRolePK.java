package myorg.queryex;

import java.io.Serializable;

public class MovieRolePK implements Serializable {
	private static final long serialVersionUID = -2134263902401216090L;
	private String movie;
	private String role;
	
	public MovieRolePK() {}
	
	public String getMovie() { return movie; }
	public MovieRolePK setMovie(String movie) {
		this.movie = movie;
		return this;
	}
	
	public String getRole() { return role; }
	public MovieRolePK setRole(String role) {
		this.role = role;
		return this;
	}

	@Override
	public int hashCode() {
		return movie==null?0:movie.hashCode() +
				role==null?0:role.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		try {
			if (this==obj) { return true; }
			if (obj==null) { return false; }
			MovieRolePK rhs = (MovieRolePK)obj;
			if (movie!=null && role!=null) {
				return movie.equals(rhs.movie) && role.equals(rhs.role);
			}
			if (movie==null && rhs.movie!=null) { return false; }
			if (role==null && rhs.role!=null) { return false; }
			return true;
		} catch (Exception ex) { return false; }
	}
	
	@Override
	public String toString() {
		return movie + " " + role;
	}
	
}
