#!/usr/bin/env bash

sshpass -p "${RTB_PASSWORD}" ssh "${RTB_USER}"@"${RTB_KAFKA_HOST}" << EOF
/opt/kafka/bin/kafka-topics.sh --create --topic user-tag-topic --partitions 12 --bootstrap-server localhost:9092
EOF