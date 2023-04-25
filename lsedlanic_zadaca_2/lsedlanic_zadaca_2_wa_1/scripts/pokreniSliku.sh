#!/bin/bash
NETWORK=lsedlanic_mreza_1

docker run -it -d \
  -p 8070:8080 \
  --network=$NETWORK \
  --ip 200.20.0.4 \
  --name=lsedlanic_payara_micro \
  --hostname=lsedlanic_payara_micro \
  lsedlanic_payara_micro:6.2023.4 \
  --deploy /opt/payara/deployments/lsedlanic_zadaca_2_wa_1-1.0.0..war \
  --contextroot lsedlanic_zadaca_2_wa_1 \
  --noCluster &

wait
