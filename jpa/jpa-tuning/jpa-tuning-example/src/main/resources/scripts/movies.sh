#/bin/sh

cat movies.list | \
#
#get passed the text header in the file
#also remove TV titles -- which start with double-quote
awk 'BEGIN { hstart=0; header=1; footer=0; }
     /^MOVIES LIST/ { hstart=1; }
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
#separate movie and release date fields based on 1+ tabs separating them
perl -pe s/'(.+?)\t+(.+)'/'\1|\2|'/ | \

#
#extract the movie title from the name -- take the multi-year release to title
perl -pe s/'(.*)\(([0-9\?]{4}(\/*I*))\)(.*)'/'\1(\2)|\1\3|\2|'/ | \

#trim the year suffix we worked with above from the date field
awk -F\| '
        function rtrim(s) { sub(/[ \t\r\n]+$/, "", s); return s }
	{
	    date=$3
            sub("/+I+","",date)
            if (date == "????") { date="" }
            printf "%s|%s|%s\n",$1,rtrim($2),date
        }' | \

sort -u | \
awk 'BEGIN {id=0;} { id+=1; printf "%s|m%d\n",$0,id}'
