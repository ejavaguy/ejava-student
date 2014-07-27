load data
infile '/home/oracle/proj/movies/directors.dat'
badfile '/home/oracle/proj/movies/directors.bad'
into table jpatune_movie_dir
fields terminated by "|" trailing nullcols
(movie_name, last_name, first_name, mod_name)
