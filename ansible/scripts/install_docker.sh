#!/usr/bin/env bash

REMOTE_DIR="/home/${RTB_USER}"
LOCAL_DIR="../docker"
DOCKER_PLAYBOOK="docker-playbook.yaml"
SWARM_PLAYBOOK="swarm-playbook.yaml"
INVENTORY="hosts"
EXTRA_VARS="ansible_user=${RTB_USER} ansible_password=${RTB_PASSWORD} ansible_ssh_extra_args='-o StrictHostKeyChecking=no'"

sshpass -p "${RTB_PASSWORD}" scp -r "${LOCAL_DIR}" "${RTB_USER}"@"${RTB_MAIN_HOST}":"${REMOTE_DIR}"

sshpass -p "${RTB_PASSWORD}" ssh "${RTB_USER}"@"${RTB_MAIN_HOST}" << EOF
cd docker
ansible-playbook --extra-vars "${EXTRA_VARS}" -i "${INVENTORY}" "${DOCKER_PLAYBOOK}"
ansible-playbook --extra-vars "${EXTRA_VARS}" -i "${INVENTORY}" "${SWARM_PLAYBOOK}"
EOF
