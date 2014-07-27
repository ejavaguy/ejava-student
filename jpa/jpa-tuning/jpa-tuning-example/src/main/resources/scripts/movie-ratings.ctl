load data
infile '/home/oracle/proj/movies/movie-ratings.dat'
badfile '/home/oracle/proj/movies/movie-ratings.bad'
into table jpatune_movie
fields terminated by "|" trailing nullcols
(name, 
title, 
release_date DATE 'YYYY', 
id, 
rating)
