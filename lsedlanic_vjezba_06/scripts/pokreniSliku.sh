#!/bin/bash
NETWORK=lsedlanic_mreza_1
docker run -it -d \
 -p 8090:8080 \
 --network=$NETWORK \
 --ip 200.20.0.2 \
 --name=lsedlanic_tomcat \
 --hostname=lsedlanic_tomcat \
 lsedlanic_tomcat:10.1.7 &
wait
