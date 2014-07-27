#!/bin/sh

cat mpaa-ratings-reasons.list | \
awk '
	function movie(s) { sub(/^MV: /, "", s); return s }
	function rating(s) { sub(/^RE: Rated /, "", s); return s }
	/MV:/ { m=movie($0)}
	/RE: Rated/{ printf "%s|%s|\n", m, $3 }' | \
sort -u
