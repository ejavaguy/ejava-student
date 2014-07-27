load data
infile '/home/oracle/proj/movies/directors-person.dat'
badfile '/home/oracle/proj/movies/directors-person.bad'
into table jpatune_director
fields terminated by "|" trailing nullcols
(last_name, first_name, mod_name)
