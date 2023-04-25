#!/bin/bash


docker stop lsedlanic_payara_micro
docker rm lsedlanic_payara_micro
./scripts/pripremiSliku.sh
./scripts/pokreniSliku.sh