#!/bin/sh                                                                                                                              

roles() {
    for base in roles; do
	wc -l $base.dat; ./$base.sh > $base.dat; wc -l $base.dat
    done
}

actors() {
    for base in roles actors-person persons; do
	wc -l $base.dat; ./$base.sh > $base.dat; wc -l $base.dat
    done
}

actorsperson() {
    for base in actors-person; do
	wc -l $base.dat; ./$base.sh > $base.dat; wc -l $base.dat
    done
}

directorsperson() {
    for base in directors-person; do
	wc -l $base.dat; ./$base.sh > $base.dat; wc -l $base.dat
    done
}

director() {
    for base in directors; do
	wc -l $base.dat; ./$base.sh > $base.dat; wc -l $base.dat
    done
}

directors() {
    for base in directors directors-person persons; do
	wc -l $base.dat; ./$base.sh > $base.dat; wc -l $base.dat
    done
}

person() {
    for base in persons; do
        wc -l $base.dat; ./$base.sh > $base.dat; wc -l $base.dat
    done
}
                                                                                                                                       
people() {
    #roles
    director
    for base in directors-person actors-person persons; do
        wc -l $base.dat; ./$base.sh > $base.dat; wc -l $base.dat
    done
}

movie() {
for base in movies; do
    wc -l $base.dat; ./$base.sh > $base.dat; wc -l $base.dat
done
}

movieratings() {
for base in movie-ratings; do
    wc -l $base.dat; ./$base.sh > $base.dat; wc -l $base.dat
done
}

movies() {
for base in movies ratings movie-ratings genres; do
    wc -l $base.dat; ./$base.sh > $base.dat; wc -l $base.dat
done
}

genres() {
for base in genres; do
    wc -l $base.dat; ./$base.sh > $base.dat; wc -l $base.dat
done
}

plot() {
for base in plot; do
    wc -l $base.dat; ./$base.sh > $base.dat; wc -l $base.dat
done
}

all() {
    people
    movies
}

for cmd in $*; do
    $cmd
done
