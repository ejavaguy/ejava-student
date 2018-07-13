package ejava.jpa.examples.tuning;

import javax.persistence.EntityManager;

import javax.persistence.EntityTransaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Assert;

public class MovieFactory {
	private static final Logger log = LoggerFactory.getLogger(MovieFactory.class);
	private EntityManager em;

	public MovieFactory setEntityManager(EntityManager em) {
		this.em = em;
		return this;
	}

	public static class SQLStatement {
		final SQLConstruct sql;
		final boolean required;
		public SQLStatement(SQLConstruct sql, boolean required) {
			this.sql = sql;
			this.required = required;
		}
	}
	
	public abstract class SQLConstruct {
		protected String name;
		protected String create;
		protected String drop;
		protected String populate;
		protected boolean required;
		public SQLConstruct(String name)             { this.name = name; }
		public SQLConstruct setCreate(String create) { this.create = create; return this; }
		public SQLConstruct setDrop(String drop)     { this.drop = drop; return this; }
		public SQLConstruct setPopulate(String populate) { this.populate = populate; return this; }		
		public SQLConstruct setRequired(boolean required) { this.required = required; return this; }
		public String getName()     { return name; }
		public String getCreate()   { return create; }
		public String getDrop()     { return drop; }
		public String getPopulate() { return populate; }
		public boolean isRequired() { return required; }
		public abstract boolean exists();
	}
	public class SQLIndex extends SQLConstruct {
		public SQLIndex(String name, String create) { 
			super(name);
			setCreate(create);
			setDrop("drop index " + name);
		}
		public boolean exists() {
			return ((Number)em.createNativeQuery(
					"select count(*) from user_indexes where index_name=?1")
					.setParameter(1, name.toUpperCase())
			        .getSingleResult()).intValue()==1;
		}
	}
	public class SQLColumn extends SQLConstruct {
		private String table;
		private String column;
		public SQLColumn(String table, String column, String create, String populate) {
			super(table+"."+column);
			this.table=table;
			this.column=column;
			setCreate(create);
			setDrop(String.format("alter table %s drop column %s", table, column));
			setPopulate(populate);
		}
		public boolean exists() {
			return ((Number)em.createNativeQuery(
					"select count(*) from user_tab_cols where table_name=?1 and column_name=?2")
					.setParameter(1, table.toUpperCase())
					.setParameter(2, column.toUpperCase())
			        .getSingleResult()).intValue()==1;
		}
	}
	public class SQLConstraintNonNull extends SQLConstruct {
		private String table;
		private String column;
		public SQLConstraintNonNull(String table, String column) {
			super(table+"."+column+" not null");
			this.table=table;
			this.column=column;
			setCreate(String.format("alter table %s modify %s not null", table, column));
			setDrop(  String.format("alter table %s modify %s null", table, column));
		}
		@Override
		public boolean exists() {
			return ((Number)em.createNativeQuery(
					"select count(*) from user_tab_cols where table_name=?1 and column_name=?2 and nullable='N'")
					.setParameter(1, table.toUpperCase())
					.setParameter(2, column.toUpperCase())
			        .getSingleResult()).intValue()==1;
		}
	}
	/*
	public class SQLConstraintUnique extends SQLConstruct {
		public SQLConstraintUnique(String name, String table, String column) { 
			super(name);
			setCreate(String.format("alter table %s add constraint %s unique (%s)", table, name, column));
			setDrop(  String.format("alter table %s drop constraint %s", table, name));
		}
		public boolean exists() {
			return ((Number)em.createNativeQuery(
					"select count(*) from user_indexes where index_name=?1")
					.setParameter(1, name.toUpperCase())
			        .getSingleResult()).intValue()==1;
		}
		
	}
	*/
	
	public SQLConstruct MOVIE_DIRECTOR_FKX = new SQLIndex("movie_director_fkx", "create index movie_director_fkx on jpatune_movie(director_id)");
	public SQLConstruct MOVIE_RATING_IDX = new SQLIndex("movie_rating_idx", "create index movie_rating_idx on jpatune_movie(rating)");
	public SQLConstruct MOVIE_RATING_RIDX = new SQLIndex("movie_rating_ridx", "create index movie_rating_ridx on jpatune_movie(rating desc)");
	public SQLConstruct MOVIE_RATING_LOWER_IDX = new SQLIndex("movie_rating_lower_idx", "create index movie_rating_lower_idx on jpatune_movie(lower(rating))");
	public SQLConstruct MOVIE_RATING_LOWER_RIDX = new SQLIndex("movie_rating_lower_ridx", "create index movie_rating_lower_ridx on jpatune_movie(lower(rating) desc)");
	public SQLConstruct MOVIE_TITLE_IDX = new SQLIndex("movie_title_idx", "create index movie_title_idx on jpatune_movie(title)");
	public SQLConstruct MOVIE_TITLE_RIDX = new SQLIndex("movie_title_ridx", "create index movie_title_ridx on jpatune_movie(title desc)");
	public SQLConstruct MOVIE_RATING_TITLE_IDX = new SQLIndex("movie_rating_title_idx", "create index movie_rating_title_idx on jpatune_movie(rating, title)");
	public SQLConstruct MOVIE_TITLE_RATING_IDX = new SQLIndex("movie_title_rating_idx", "create index movie_title_rating_idx on jpatune_movie(title, rating)");
	public SQLConstruct MOVIE_TITLE_RDATE_IDX = new SQLIndex("movie_title_rdate_idx", "create index movie_title_rdate_idx on jpatune_movie(title, release_date)");
	public SQLConstruct MOVIE_TITLE_RDATE_ID_IDX = new SQLIndex("movie_title_rdate_id_idx", "create index movie_title_rdate_id_idx on jpatune_movie(title, release_date, id)");
	public SQLConstruct MOVIE_RDATE_IDX = new SQLIndex("movie_rdate_idx", "create index movie_rdate_idx on jpatune_movie(release_date)");
	public SQLConstruct GENRE_MOVIE_FKX = new SQLIndex("genre_movie_fkx", "create index genre_movie_fkx on jpatune_moviegenre(movie_id)");
	public SQLConstruct MOVIEROLE_ACTOR_FKX = new SQLIndex("movierole_actor_fkx", "create index movierole_actor_fkx on jpatune_movierole(actor_id)");
	public SQLConstruct MOVIEROLE_MOVIE_FKX = new SQLIndex("movierole_movie_fkx", "create index movierole_movie_fkx on jpatune_movierole(movie_id)");

	public SQLConstruct MOVIE_UTITLE = new SQLColumn("jpatune_movie", "utitle", "alter table jpatune_movie add utitle varchar2(256)",
			                                                                    "update jpatune_movie set utitle=concat(concat(concat(title,'('),id),')')");
	public SQLConstruct MOVIE_UTITLE_NONNULL = new SQLConstraintNonNull("jpatune_movie", "utitle");
	public SQLConstruct MOVIE_UTITLE_IDX = new SQLIndex("movie_utitle_idx", "create index movie_utitle_idx on jpatune_movie(utitle)");
	public SQLConstruct MOVIE_UTITLE_UDX = new SQLIndex("movie_utitle_udx", "create unique index movie_utitle_udx on jpatune_movie(utitle)");
	//public SQLConstruct MOVIE_UTITLE_UNIQUE = new SQLConstraintUnique("movie_utitle_unique", "jpatune_movie", "utitle");
	
	public SQLConstruct MOVIE_ROLE_IDX = new SQLIndex("movie_role_idx", "create index movie_role_idx on jpatune_movierole(movie_role)");
	public SQLConstruct MOVIE_ROLE_MOVIE_FDX = new SQLIndex("movie_role_movie_fdx", "create index movie_role_movie_fdx on jpatune_movierole(movie_id)");
	public SQLConstruct MOVIE_ROLE_MOVIE_CDX = new SQLIndex("movie_role_movie_cdx", "create index movie_role_movie_cdx on jpatune_movierole(movie_role, movie_id)");
    public SQLConstruct MOVIE_ROLE_ACTOR_MOVIE_CDX = new SQLIndex("movierole_actor_movie_cdx", "create index movierole_actor_movie_cdx on jpatune_movierole(actor_id, movie_id)");
	public SQLConstruct MOVIE_ROLE_MOVIE_ACTOR_CDX = new SQLIndex("movierole_movie_actor_cdx", "create index movierole_movie_actor_cdx on jpatune_movierole(movie_id, actor_id)");
    
	public void populate() {
	}

	public void cleanup() {
		dropConstructs();
	}

	public MovieFactory executeSQL(SQLConstruct[] constructs) {
		log.info("------------------------------------------------------------");
		SQLStatement[] statements = new SQLStatement[constructs.length];
		for (int i=0; i< constructs.length; i++) {
			statements[i] = new SQLStatement(constructs[i], true);
		}
		executeSQL(statements, false);
		log.info("------------------------------------------------------------");
		return this;
	}

	public MovieFactory executeSQL(SQLStatement sql[], boolean drop) {
		for (SQLStatement s: sql) {
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			StringBuilder text = new StringBuilder(drop ? s.sql.getDrop() : s.sql.getCreate());
			try {
				boolean exists = s.sql.exists();
				if (!drop && !exists) {
					log.info("{}",text);
					em.createNativeQuery(s.sql.getCreate()).executeUpdate();
					if (s.sql.getPopulate()!=null) {
						text=new StringBuilder(s.sql.getPopulate());
						log.info("{}",text);
						em.createNativeQuery(s.sql.getPopulate()).executeUpdate();
					}
				} else if (drop && exists) {
					log.info("{}", text);
					em.createNativeQuery(s.sql.getDrop()).executeUpdate();
				} else {
					text.append(" (noop)");
					log.debug("{}",text);
				}
			} catch (Exception ex) {
				if (s.required) {
					log.error("{}",text);
					log.error("failed:" + s.sql, ex);
					throw new RuntimeException("failed:" + s.sql, ex);
				}
			}
			if (!tx.getRollbackOnly()) { tx.commit(); }
			else { tx.rollback(); }
		}
		return this;
	}

	public MovieFactory executeSQL(String[] sql, boolean failOnError) {
		for (String s: sql) {
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			try {
				em.createNativeQuery(s).executeUpdate();
			} catch (Exception ex) {
				if (failOnError) {
					log.error("failed:" + s, ex);
					throw new RuntimeException("failed:" + s, ex);
				}
			}
			if (!tx.getRollbackOnly()) { tx.commit(); }
			else { tx.rollback(); }
		}
		return this;
	}
	
	public MovieFactory assertConstructs(SQLConstruct[] sql) {
		for (SQLConstruct s: sql) {
			Assert.assertTrue(s.getName() + " does not exist", s.exists());
		}
		return this;
	}
	
	
	public MovieFactory dropConstructs() {
		SQLStatement sql[] = new SQLStatement[]{
			new SQLStatement(MOVIE_DIRECTOR_FKX, false),
			new SQLStatement(GENRE_MOVIE_FKX, false),
			new SQLStatement(MOVIEROLE_ACTOR_FKX, false),
			new SQLStatement(MOVIEROLE_MOVIE_FKX, false),
			new SQLStatement(MOVIE_RATING_IDX, false),
			new SQLStatement(MOVIE_RATING_RIDX, false),
			new SQLStatement(MOVIE_RATING_LOWER_IDX, false),
			new SQLStatement(MOVIE_RATING_LOWER_RIDX, false),
			new SQLStatement(MOVIE_TITLE_IDX, false),
			new SQLStatement(MOVIE_TITLE_RIDX, false),
			new SQLStatement(MOVIE_RATING_TITLE_IDX, false),
			new SQLStatement(MOVIE_TITLE_RATING_IDX, false),
			new SQLStatement(MOVIE_UTITLE_IDX, false),
			new SQLStatement(MOVIE_UTITLE_NONNULL, false),
			new SQLStatement(MOVIE_UTITLE_IDX, false),
			new SQLStatement(MOVIE_UTITLE_UDX, false),
			new SQLStatement(MOVIE_TITLE_RDATE_IDX, false),
			new SQLStatement(MOVIE_TITLE_RDATE_ID_IDX, false),
			new SQLStatement(MOVIE_RDATE_IDX, false),
			new SQLStatement(MOVIE_ROLE_IDX, false),
			new SQLStatement(MOVIE_ROLE_MOVIE_FDX, false),
			new SQLStatement(MOVIE_ROLE_MOVIE_CDX, false),
			new SQLStatement(MOVIE_ROLE_ACTOR_MOVIE_CDX, false),
			new SQLStatement(MOVIE_ROLE_MOVIE_ACTOR_CDX, false)
		};
		executeSQL(sql, true);
		return this;
	}
	
	public MovieFactory createFKIndexes() {
		SQLConstruct sql[] = new SQLConstruct[]{
				MOVIE_DIRECTOR_FKX, 
				GENRE_MOVIE_FKX,
				MOVIEROLE_ACTOR_FKX,
				MOVIEROLE_MOVIE_FKX
		};
		executeSQL(sql);
		return this;
	}
	
	public MovieFactory flush() {
		executeSQL(new String[]{
				"alter system flush shared_pool",
				"alter system flush buffer_cache"
		}, true);
		return this;
	}

}
