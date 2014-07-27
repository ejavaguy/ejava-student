--------------------------------------------------------
--  DDL for PERSON
--------------------------------------------------------
--create table JPATUNE_PERSON (
--    ID varchar2(36 char) not null,
--    BIRTH_DATE date,
--    FIRST_NAME varchar2(128 char),
--    LAST_NAME varchar2(128 char),
--    MOD_NAME varchar2(32 char),
--    primary key (ID),
--    unique (LAST_NAME, FIRST_NAME, MOD_NAME)
--);
  
--------------------------------------------------------
--  DDL for ACTOR
--------------------------------------------------------
--create table JPATUNE_ACTOR (
--    PERSON_ID varchar2(36 char) not null,
--    primary key (PERSON_ID)
--);
drop index actor_person_fkx;

--------------------------------------------------------
--  DDL for DIRECTOR
--------------------------------------------------------
--create table JPATUNE_DIRECTOR (
--    PERSON_ID varchar2(36 char) not null,
--    primary key (PERSON_ID)
--);
drop index director_person_fkx;


--------------------------------------------------------
--  DDL for MOVIE
--------------------------------------------------------
--create table JPATUNE_MOVIE (
--    ID varchar2(36 char) not null,
--    MINUTES number(10,0),
--    PLOT varchar2(4000 char),
--    RATING varchar2(6 char),
--    RELEASE_DATE date,
--    TITLE varchar2(32 char) not null,
--    DIRECTOR_ID varchar2(36 char),
--    primary key (ID)
--);
drop index movie_director_fkx;

--------------------------------------------------------
--  DDL for MOVIEGENRE
--------------------------------------------------------
--create table QUERYEX_MOVIEGENRE (
--    MOVIE_ID varchar2(36 char) not null,
--    GENRE varchar2(20 char),
--    unique (MOVIE_ID, GENRE)
--);
drop index genre_movie_fkx;

--------------------------------------------------------
--  DDL for MOVIEROLE
--------------------------------------------------------
--create table JPATUNE_MOVIEROLE (
--    MOVIE_ID varchar2(255 char),
--    MOVIE_ROLE varchar2(32 char) not null,
--    ACTOR_ID varchar2(36 char) not null,
--    primary key (MOVIE_ID, MOVIE_ROLE)
--);
drop index movierole_actor_fkx;
drop index movierole_movie_fkx;

