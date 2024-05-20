#!/usr/bin/env bash

PASSWORD="***REMOVED***"
USER="st122"
HOST="st122vm101.rtb-lab.pl"
DOCKERHUB_USERNAME="integraal"
BACKEND_REPLICAS=10
AGGREGATOR_REPLICAS=2

sshpass -p "${PASSWORD}" ssh ${USER}@${HOST} << EOF
sudo docker service create --name allezon-backend -p 8080:8080 --replicas $BACKEND_REPLICAS $DOCKERHUB_USERNAME/allezon-backend
sudo docker service create --name allezon-aggregator -p 9090:9090 --replicas $AGGREGATOR_REPLICAS $DOCKERHUB_USERNAME/allezon-aggregator
EOF