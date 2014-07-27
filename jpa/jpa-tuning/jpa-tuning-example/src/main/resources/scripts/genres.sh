#!/bin/sh

cat genres.list | \
#
#get passed the header
#also remove TV titles -- which start with double-quote
awk 'BEGIN { hstart=0; header=1; footer=0; }
     /^8: THE GENRES LIST/ { hstart=1; }
            { 
              #printf "hstart=%d, header=%d, footer=%d, %s\n", hstart, header, footer, $0; 
              if (hstart>0) { hstart += 1; }
              if (hstart>4) { header = 0; }
	      if (header==0 && footer==0 && !match($0,"^\"")) {
		  print; 
	      }
            }
' | \

#
#get rid of extra information that appears after the (YYYY) date in the movie name prior to first tab
#be sure to leave at least 1 tab still between the groups
perl -pe s/'(\([0-9\?]{4}\/*[IV]*\)).*?\t'/'\1\t'/ | \

#
#separate movie and genre fields based on 1+ tabs separating them
perl -pe s/'(.+?)\t+(.+)'/'\1|\2'/ | \

sort -u


