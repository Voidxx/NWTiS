#!/bin/bash
docker run -it -d \
 --name=lsedlanic_vjezba_04_1S \
 --mount source=lsedlanic_podaci,target=/usr/app/podaci \
 lsedlanic_vjezba_04_1:1.0.0 &
wait 
