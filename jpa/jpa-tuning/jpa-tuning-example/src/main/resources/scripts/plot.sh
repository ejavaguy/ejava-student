#!/bin/sh

awk '
	function movie(s) { sub(/^MV: /, "", s); return s }
	function plot(s) { sub(/^PL: /, "", s); return s }
        BEGIN { ignore=1; }
	/^MV: / { 
                 m=movie($0); 
                 ignore=match(m,"^\"");
               }
	/^PL: /{ 
                 if (!ignore) {
                     if (length(p)==0) { p=plot($0)} else { p=p" "plot($0) } 
                 }
               }
        /^--------/ { if (!ignore && length(m) > 0) {
                     printf "%s|%d|%s\n", m, length(p), substr(p,1,4000); 
                     m=""; p=""; 
                 }
               }' plot.list | \
#
#delete any extra information in the movie name after the (YYYY) in the first field
perl -pe s/'(\([0-9\?]{4}\/*[IV]*\)).*?([\|\[])'/'\1\2'/ 
