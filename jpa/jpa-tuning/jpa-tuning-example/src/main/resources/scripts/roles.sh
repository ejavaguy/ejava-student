#!/bin/sh

cat actors.list | \
#
# get beyond the header block and stop before hitting footer
# also eliminate blank rows
awk 'BEGIN { hstart=0; header=1; footer=0; }
     /^THE ACTORS LIST/ { hstart=1; }
     /------------------------/ { if (header==0) footer=1; }
            { 
              #printf "hstart=%d, header=%d, footer=%d, %s\n", hstart, header, footer, $0; 
              if (hstart>0) { hstart += 1; }
              if (hstart>6) { header = 0; }
              if (header==0 && footer==0 && length($0)>0) { print; }
            }
' | \

#break the actor and movie[role] apart based on the tabs between them
perl -pe s/'(.+?)\t+(.+)'/'\1|\2'/ | \

#the subsequent lines have just \tmovie
perl -pe s/'^\t+(.+)'/'\1'/ | \

#associate the actor from the leading line to subsequent lines
#add a default role for a missing role field
#don't print TV shows -- they start with a double quote character
awk -F\| '\
    function ltrim(s) { sub(/^[ \t\r\n]+/, "", s); return s }
    function rtrim(s) { sub(/[ \t\r\n]+$/, "", s); return s }
    function trim(s)  { return rtrim(ltrim(s)); }
    { 
      if (length($1) > 0) { 
	  split($1, name, ",");
	  last=name[1];
          if (length(name[2]) > 0) {
		  split(name[2], name, " ");
		  first=name[1];
		  mod=name[2];
          } else {
		  split(name[1], name, " ");
		  last=name[1];
		  mod=name[2];
          }
	  sub("\\(","",mod);
	  sub("\\)","",mod);
      }
      movie=$2
      if (!match(movie,"\\[.+\\]")) {
          movie=$2 "[unknown role]";
      }
      if (!match($2,"^\"")) {
	  printf "%s|%s|%s|%s|\n", movie, last, first, mod; 
      }
    }'  | \

#clean up some stuff that looks like footnotes found in various fields
perl -pe s/'\ +\<[0-9]*\>'/''/ | \

#
#convert the role to its own field
perl -pe s/'(\([0-9\?]{4}\/*[IV]*\)).*\[(.*)\]\|(.*)'/'\1|\2|\3'/ | \

#
#delete any extra information in the movie name after the (YYYY) in the first field
perl -pe s/'(\([0-9\?]{4}\/*[IV]*\)).*?([\|\[])'/'\1\2'/ | \

#
#add an id to each role since movie and movie_role is not unique
awk 'BEGIN {id=0} { id+=1; printf "r%d|%s\n",id,$0; }'
