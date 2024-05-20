#!/usr/bin/env bash

PASSWORD="***REMOVED***"
USER="st122"
HOST="st122vm101.rtb-lab.pl"
REMOTE_DIR="/home/${USER}"
LOCAL_DIR="../kafka"
KAFKA_PLAYBOOK="kafka.yaml"
INVENTORY="hosts"
EXTRA_VARS="ansible_user=${USER} ansible_password=${PASSWORD} ansible_ssh_extra_args='-o StrictHostKeyChecking=no'"

sshpass -p "${PASSWORD}" scp -r "${LOCAL_DIR}" ${USER}@${HOST}:${REMOTE_DIR}

sshpass -p "${PASSWORD}" ssh ${USER}@${HOST} << EOF
cd kafka
ansible-playbook --extra-vars "${EXTRA_VARS}" -i ${INVENTORY} ${KAFKA_PLAYBOOK}
EOF
