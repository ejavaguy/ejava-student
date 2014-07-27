load data
infile '/home/oracle/proj/movies/persons.dat'
badfile '/home/oracle/proj/movies/persons.bad'
into table jpatune_person
fields terminated by "|" trailing nullcols
(id, last_name, first_name, mod_name)
