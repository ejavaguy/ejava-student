load data
infile '/home/oracle/proj/movies/genres.dat'
badfile '/home/oracle/proj/movies/genres.bad'
into table jpatune_moviegenre
fields terminated by "|" 
(movie_name, genre)
