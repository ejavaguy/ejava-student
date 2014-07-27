#!/bin/sh

#
# extract the actor name starting with field 3
perl -pe s/'(.*?\|){2}'/''/ roles.dat | \
sort -u
