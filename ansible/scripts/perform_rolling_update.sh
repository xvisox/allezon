#!/usr/bin/env bash

DOCKERHUB_USERNAME="integraal"
PASSWORD="***REMOVED***"
USER="st122"
HOST="st122vm101.rtb-lab.pl"

sshpass -p "${PASSWORD}" ssh ${USER}@${HOST} << EOF
sudo docker service update --image ${DOCKERHUB_USERNAME}/allezon-backend:latest allezon-backend
sudo docker service update --image ${DOCKERHUB_USERNAME}/allezon-aggregator:latest allezon-aggregator
EOF
