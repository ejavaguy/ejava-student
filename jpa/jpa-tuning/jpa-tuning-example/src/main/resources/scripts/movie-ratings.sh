#!/bin/sh

if [ ! -f movies.dat ]; then
	./movies.sh > movies.dat
fi
if [ ! -f ratings.dat ]; then
	./ratings.sh > ratings.dat
fi

join -t\| -1 1 -2 1 -a 1 movies.dat ratings.dat
