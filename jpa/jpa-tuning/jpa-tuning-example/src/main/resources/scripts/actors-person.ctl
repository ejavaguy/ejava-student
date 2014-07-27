load data
infile '/home/oracle/proj/movies/actors-person.dat'
badfile '/home/oracle/proj/movies/actors-person.bad'
into table jpatune_actor
fields terminated by "|" trailing nullcols
(last_name, first_name, mod_name)
