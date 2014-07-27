load data
infile '/home/oracle/proj/movies/roles.dat'
badfile '/home/oracle/proj/movies/roles.bad'
into table jpatune_movierole
fields terminated by "|" trailing nullcols
(id, movie_name, movie_role, last_name, first_name, mod_name)
