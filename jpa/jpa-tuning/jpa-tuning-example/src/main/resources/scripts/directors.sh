#!/bin/sh

cat directors.list | \
#
#get passed the text header in the file
awk 'BEGIN { hstart=0; header=1; footer=0; }
     /^THE DIRECTORS LIST/ { hstart=1; }
     /------------------------/ { if (header==0) footer=1; }
            { 
              #printf "hstart=%d, header=%d, footer=%d, %s\n", hstart, header, footer, $0; 
              if (hstart>0) { hstart += 1; }
              if (hstart>5) { header = 0; }
              if (header==0 && footer==0 && length($0)>0) { print; }
            }
' | \

#
#get rid of extra information that appears after the (YYYY) date in the movie name at end of line
perl -pe s/'(\([0-9\?]{4}\/*[IV]*\)).*'/'\1'/ | \

#
#separate director and title fields based on 1+ tabs separating them
#the start of a new author has author\tmovie -- stop field1 at first tab
perl -pe s/'(.+?)\t+(.+)'/'\1|\2|'/ | \

#the subsequent lines have just \tmovie
perl -pe s/'^\t+(.+)'/'\1|'/ | \

#
#copy director from starting line and copy to subsequent lines
#chop the director name into first and lastname columns
#save off the modifier that seems to de-dup common names
#print the movie name as the first column
#don't print movies -- they start with a double quote
awk -F\| '\
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
      if (!match($2,"^\"") && length($2) > 0) {
          printf "%s|%s|%s|%s|\n", $2, last, first, mod; 
      }
    }' | \
sort
