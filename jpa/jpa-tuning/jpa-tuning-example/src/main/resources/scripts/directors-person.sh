#!/bin/sh

#
# extract the director name
perl -pe s/'.*?\|'/''/ directors.dat | \
sort -u

