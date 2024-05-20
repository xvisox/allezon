#!/usr/bin/env bash

PASSWORD="***REMOVED***"
USER="st122"
HOST="st122vm107.rtb-lab.pl"

sshpass -p "${PASSWORD}" ssh ${USER}@${HOST} << EOF
/opt/kafka/bin/kafka-topics.sh --create --topic user-tag-topic --partitions 10 --bootstrap-server localhost:9092
EOF