package ejava.jpa.examples.tuning.bo;

public enum MovieRating {
	G,
	PG, 
	PG13("PG-13"),
	R, 
	NC17("NC-17");
	
	private final String mpaa;
	MovieRating(){ mpaa=this.name(); }
	MovieRating(String alt) { mpaa=alt; }
	public String mpaa() { return mpaa; }
	
	public static MovieRating getFromMpaa(String mpaa) {
		if (mpaa==null) { return null; }
		for (MovieRating rating : values()) {
			if (rating.mpaa().equals(mpaa)) { return rating; }
		}
		throw new IllegalArgumentException("no Movie Rating mapping for " + mpaa);
	}
}
