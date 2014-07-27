load data
infile '/home/oracle/proj/movies/plot.dat'
badfile '/home/oracle/proj/movies/plot.bad'
into table jpatune_movieplot
when (movie_name != BLANKS)
fields terminated by "|" 
(movie_name, 
plot_length, 
plot char(4000)
)
