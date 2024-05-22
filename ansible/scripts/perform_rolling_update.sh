#!/usr/bin/env bash

sshpass -p "${RTB_PASSWORD}" ssh "${RTB_USER}"@"${RTB_MAIN_HOST}" << EOF
sudo docker service update --image "${DOCKERHUB_USERNAME}"/allezon-backend:latest allezon-backend
sudo docker service update --image "${DOCKERHUB_USERNAME}"/allezon-aggregator:latest allezon-aggregator
EOF
