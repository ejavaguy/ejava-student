#!/bin/sh -x

drop() {
echo exit | sqlplus ejava/password << EOS
set echo on
drop table JPATUNE_MOVIEROLE purge;
drop table JPATUNE_ACTOR purge;
drop table JPATUNE_MOVIEGENRE purge;
drop table JPATUNE_MOVIE purge;
drop table JPATUNE_DIRECTOR purge;
drop table JPATUNE_PERSON purge;
commit;
EOS
}

cleanup() {
#remove temporary tables and non-core indexes/constraints
echo exit | sqlplus ejava/password << EOS
set echo on

alter table jpatune_director drop constraint director_names_unique;

alter table jpatune_actor drop constraint actor_names_unique;

drop index movie_director_fkx;

alter table jpatune_movie drop column name;

drop index genre_movie_fkx;

alter table jpatune_movierole drop constraint movierole_names_unique;
alter table jpatune_movierole drop column movie_name;
alter table jpatune_movierole drop column last_name;
alter table jpatune_movierole drop column first_name;
alter table jpatune_movierole drop column mod_name;
drop index movierole_actor_fkx;
drop index movierole_movie_fkx;

commit;
EOS
}

index() {
#remove temporary tables and non-core indexes/constraints
echo exit | sqlplus ejava/password << EOS
set echo on

create index movie_director_fkx on jpatune_movie(director_id);
create index genre_movie_fkx on jpatune_moviegenre(movie_id);

create index movierole_actor_fkx on jpatune_movierole(actor_id);
create index movierole_movie_fkx on jpatune_movierole(movie_id);

commit;
EOS
}

person() {
echo exit | sqlplus ejava/password << EOS
set echo on
drop table JPATUNE_PERSON purge;
create table JPATUNE_PERSON (
    ID varchar2(36 char) not null,
    BIRTH_DATE date,
    LAST_NAME varchar2(128 char) not null,
    FIRST_NAME varchar2(128 char),
    MOD_NAME varchar2(32 char)
);
alter table jpatune_person add constraint person_pk PRIMARY KEY (id);
alter table jpatune_person add constraint person_names_unique unique (LAST_NAME, FIRST_NAME, MOD_NAME);
commit;
EOS

sqlldr ejava/password ERRORS=2000 CONTROL=persons.ctl LOG=persons.log; tail -n 20 persons.log >> load.log
}

director() {
echo exit | sqlplus ejava/password << EOS
set echo on
drop table JPATUNE_MOVIEGENRE purge;
drop table JPATUNE_MOVIE purge;
drop table JPATUNE_MOVIE_DIR purge;
drop table jpatune_director purge;
create table JPATUNE_DIRECTOR (
    PERSON_ID varchar2(36 char),
    LAST_NAME varchar2(128 char) not null,
    FIRST_NAME varchar2(128 char),
    MOD_NAME varchar2(32 char)
);
alter table jpatune_director add constraint director_names_unique unique (LAST_NAME, FIRST_NAME, MOD_NAME);

commit;
EOS

sqlldr ejava/password ERRORS=10 CONTROL=directors-person.ctl LOG=directors-person.log; tail -n 20 directors-person.log >> load.log

echo exit | sqlplus ejava/password << EOS
set echo on
update jpatune_director d
set d.person_id=(select p.id
   from jpatune_person p
   where d.last_name=p.last_name and 
         (d.first_name is null and p.first_name is null or d.first_name=p.first_name) and
         (d.mod_name is null and p.mod_name is null or d.mod_name=p.mod_name)
         );

alter table jpatune_director add constraint director_pk PRIMARY KEY (person_id);
alter table jpatune_director add constraint director_person_fk FOREIGN KEY(person_id) references jpatune_person(id);
commit;
EOS
}



movie() {
echo exit | sqlplus ejava/password << EOS
set echo on
drop table JPATUNE_MOVIEGENRE purge;
drop table JPATUNE_MOVIE purge;
drop table JPATUNE_MOVIE_DIR purge;
create table JPATUNE_MOVIE (
    ID varchar2(36 char) not null,
--    MINUTES number(10,0),
    RATING varchar2(6),
    RELEASE_DATE date,
    TITLE varchar2(256) not null,
    DIRECTOR_ID varchar2(36),
    NAME varchar2(256) not null
);
alter table jpatune_movie add constraint movie_pk PRIMARY KEY (id);
alter table jpatune_movie add constraint movie_name_unique unique (NAME);

create table JPATUNE_MOVIE_DIR (
    MOVIE_NAME varchar2(256) not null,
    LAST_NAME varchar2(128 char) not null,
    FIRST_NAME varchar2(128 char),
    MOD_NAME varchar2(32 char),
    unique(MOVIE_NAME)
);
commit;
EOS
sqlldr ejava/password ERRORS=100 CONTROL=movie-ratings.ctl LOG=movie-ratings.log; tail -n 20 movie-ratings.log >> load.log

sqlldr ejava/password ERRORS=100000 CONTROL=directors.ctl LOG=directors.log; tail -n 20 directors.log >> load.log

echo exit | sqlplus ejava/password << EOS
set echo on
update jpatune_movie m
set m.director_id = (
    select d.person_id 
    from jpatune_director d 
    join jpatune_movie_dir md on md.last_name=d.last_name and 
         ((md.first_name is null and d.first_name is null) or md.first_name=d.first_name) and 
         ((md.mod_name is null and d.mod_name is null) or md.mod_name=d.mod_name)
    where md.movie_name = m.name
    );
alter table jpatune_movie add constraint movie_director_fk FOREIGN KEY(director_id) references jpatune_director(person_id);
create index movie_director_fkx on jpatune_movie(director_id);

drop table JPATUNE_MOVIE_DIR purge;
alter table jpatune_movie modify name varchar2(256) null;
commit;
EOS
}




genre() {
echo exit | sqlplus ejava/password <<EOS
set echo on
drop table JPATUNE_MOVIEGENRE purge;
create table JPATUNE_MOVIEGENRE (
    MOVIE_ID varchar2(36 char),
    GENRE varchar2(20 char) not null,
    MOVIE_NAME varchar2(256) not null
);
alter table jpatune_moviegenre add constraint moviegenre_refs_unique unique (MOVIE_NAME, GENRE);
commit;
EOS

sqlldr ejava/password ERRORS=0 CONTROL=genres.ctl LOG=genres.log; tail -n 20 genres.log >> load.log

echo exit | sqlplus ejava/password << EOS
set echo on
create index movie_name_idx on jpatune_movie(name,id);
update jpatune_moviegenre g
set g.movie_id = (
    select m.id from jpatune_movie m where m.name = g.movie_name
);
drop index movie_name_idx;

--try to match movies by converting latin characters in movie name to wildcard chars
--create index movie_name_idx on jpatune_movie(REGEXP_REPLACE(ASCIISTR(name), '\\[[:xdigit:]]{4}', '_'));
--update jpatune_moviegenre g
--set g.movie_id=(
--    select id from jpatune_movie m
--    where g.movie_name like REGEXP_REPLACE(ASCIISTR(m.name), '\\[[:xdigit:]]{4}', '_') and rownum=1)
--where g.movie_id is null; 
--drop index movie_name_idx;

--get rid the of remaining that could not be matched
delete from jpatune_moviegenre g
where g.movie_id is null;

alter table jpatune_moviegenre modify movie_id varchar2(36) not null;
alter table jpatune_moviegenre add constraint moviegenre_unique UNIQUE (movie_id, genre);
alter table jpatune_moviegenre add constraint moviegenre_movie_fk FOREIGN KEY(movie_id) references jpatune_movie(id);
create index genre_movie_fkx on jpatune_moviegenre(movie_id);

alter table jpatune_moviegenre drop constraint moviegenre_refs_unique;
alter table jpatune_moviegenre drop column movie_name;
commit;
EOS
}




actor() {
echo exit | sqlplus ejava/password << EOS
set echo on
drop table JPATUNE_ACTOR purge;
create table JPATUNE_ACTOR (
    PERSON_ID varchar2(36 char),
    LAST_NAME varchar2(128 char) not null,
    FIRST_NAME varchar2(128 char),
    MOD_NAME varchar2(32 char)
);
alter table jpatune_actor add constraint actor_names_unique unique (last_name, first_name, mod_name);

commit;
EOS
sqlldr ejava/password ERRORS=10000 CONTROL=actors-person.ctl LOG=actors-person.log; tail -n 20 actors-person.log >> load.log

echo exit | sqlplus ejava/password << EOS
set echo on
update jpatune_actor a
set a.person_id=(select p.id
   from jpatune_person p
   where a.last_name=p.last_name and 
         (a.first_name is null and p.first_name is null or a.first_name=p.first_name) and
         (a.mod_name is null and p.mod_name is null or a.mod_name=p.mod_name)
         );

alter table jpatune_actor add constraint actor_pk PRIMARY KEY (person_id);
alter table jpatune_actor add constraint actor_person_fk FOREIGN KEY(person_id) references jpatune_person(id);

commit;
EOS
}



role() {
echo exit | sqlplus ejava/password << EOS
set echo on
drop table JPATUNE_MOVIEROLE purge;
create table JPATUNE_MOVIEROLE (
    ID varchar2(36 char) not null,
    MOVIE_ID varchar2(36 char),
    MOVIE_ROLE varchar2(512 char) not null,
    ACTOR_ID varchar2(36 char),
    LAST_NAME varchar2(128 char) not null,
    FIRST_NAME varchar2(128 char),
    MOD_NAME varchar2(32 char),
    MOVIE_NAME varchar2(256) not null
);
alter table jpatune_movierole add constraint movierole_pk PRIMARY KEY (id);
alter table jpatune_movierole add constraint movierole_names_unique unique(MOVIE_NAME, MOVIE_ROLE, LAST_NAME, FIRST_NAME, MOD_NAME);

commit;
EOS

sqlldr ejava/password ERRORS=10000 CONTROL=roles.ctl LOG=roles.log; tail -n 20 roles.log >> load.log

echo exit | sqlplus ejava/password << EOS
set echo on
update jpatune_movierole r
set r.actor_id = (select person_id from jpatune_actor a
   where a.last_name=r.last_name and
         (a.first_name is null and r.first_name is null or a.first_name=r.first_name) and
         (a.mod_name is null and r.mod_name is null or a.mod_name=r.mod_name)
         );
commit;

alter table jpatune_movierole add constraint movierole_actor_fk FOREIGN KEY(actor_id) references jpatune_actor(person_id);
create index movierole_actor_fkx on jpatune_movierole(actor_id);
commit;

update jpatune_movierole r
set r.movie_id = (select id from jpatune_movie m where m.name=r.movie_name);
delete jpatune_movierole where movie_id is null;
commit;

alter table jpatune_movierole modify movie_id varchar2(36) not null;
alter table jpatune_movierole add constraint movierole_movie_fk FOREIGN KEY(movie_id) references jpatune_movie(id);
create index movierole_movie_fkx on jpatune_movierole(movie_id);

commit;

EOS
}



plot() {
echo exit | sqlplus ejava/password << EOS
set echo on
drop table JPATUNE_MOVIEPLOT purge;
alter table JPATUNE_MOVIE drop column plot;
create table JPATUNE_MOVIEPLOT (
    MOVIE_NAME varchar2(256) not null,
    PLOT_LENGTH int not null,
    PLOT varchar2(4000 char),
    primary key(MOVIE_NAME)
);
commit;
EOS

sqlldr ejava/password ERRORS=1000000 CONTROL=plot.ctl LOG=plot.log; tail -n 20 plot.log >> load.log

echo exit | sqlplus ejava/password << EOS
set echo on

alter table JPATUNE_MOVIE add plot varchar2(4000 char);

update jpatune_movie m
set m.plot = (select p.plot from jpatune_movieplot p where p.movie_name = m.name);

commit;
drop table JPATUNE_MOVIEPLOT purge;
commit;
EOS
}

runningtime() {
echo exit | sqlplus ejava/password << EOS
set echo on

alter table JPATUNE_MOVIE add minutes number(10,0);

commit;
EOS
}

all() {
    drop
    person
    director
    movie
    genre
    actor
    role
    plot
    runningtime
    cleanup
}

echo $* > load.log
for cmd in $*; do
   echo "++++++++++ $cmd ++++++++++++"
   $cmd
   echo "---------- $cmd ------------"
done
