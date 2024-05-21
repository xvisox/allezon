#!/usr/bin/env bash

DOCKERHUB_USERNAME="xvisox"
PASSWORD="***REMOVED***"
USER="st119"
HOST="st119vm101.rtb-lab.pl"

sshpass -p "${PASSWORD}" ssh ${USER}@${HOST} << EOF
sudo docker service update --image ${DOCKERHUB_USERNAME}/allezon-backend:latest allezon-backend
sudo docker service update --image ${DOCKERHUB_USERNAME}/allezon-aggregator:latest allezon-aggregator
EOF
