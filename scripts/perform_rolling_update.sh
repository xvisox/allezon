#!/usr/bin/env bash

PASSWORD="***REMOVED***"
USER="st122"
HOST="st122vm101.rtb-lab.pl"

sshpass -p "${PASSWORD}" ssh ${USER}@${HOST} << EOF
sudo docker service update --image integraal/allezon:latest allezon
EOF
