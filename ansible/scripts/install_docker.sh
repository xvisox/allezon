#!/usr/bin/env bash

PASSWORD="***REMOVED***"
USER="st119"
HOST="st119vm101.rtb-lab.pl"
REMOTE_DIR="/home/${USER}"
LOCAL_DIR="../docker"
DOCKER_PLAYBOOK="docker-playbook.yaml"
SWARM_PLAYBOOK="swarm-playbook.yaml"
INVENTORY="hosts"
EXTRA_VARS="ansible_user=${USER} ansible_password=${PASSWORD} ansible_ssh_extra_args='-o StrictHostKeyChecking=no'"

sshpass -p "${PASSWORD}" scp -r "${LOCAL_DIR}" ${USER}@${HOST}:${REMOTE_DIR}

sshpass -p "${PASSWORD}" ssh ${USER}@${HOST} << EOF
cd docker
ansible-playbook --extra-vars "${EXTRA_VARS}" -i ${INVENTORY} ${DOCKER_PLAYBOOK}
ansible-playbook --extra-vars "${EXTRA_VARS}" -i ${INVENTORY} ${SWARM_PLAYBOOK}
EOF
