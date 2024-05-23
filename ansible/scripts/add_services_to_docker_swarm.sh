#!/usr/bin/env bash

BACKEND_REPLICAS=10
AGGREGATOR_REPLICAS=2

sshpass -p "${RTB_PASSWORD}" ssh "${RTB_USER}"@"${RTB_MAIN_HOST}" << EOF
sudo docker service create --name allezon-backend -p 8080:8080 --replicas $BACKEND_REPLICAS $DOCKERHUB_USERNAME/allezon-backend
sudo docker service create --name allezon-aggregator -p 9090:9090 --replicas $AGGREGATOR_REPLICAS $DOCKERHUB_USERNAME/allezon-aggregator
EOF