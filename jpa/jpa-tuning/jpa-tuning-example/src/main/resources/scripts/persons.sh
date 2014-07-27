#!/bin/sh

sort -u actors-person.dat directors-person.dat | \
awk 'BEGIN {id=0} { id+=1; printf "p%d|%s\n",id,$0; }'
