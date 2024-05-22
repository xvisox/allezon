#!/usr/bin/env bash

sshpass -p "${RTB_PASSWORD}" ssh "${RTB_USER}"@"${RTB_MAIN_HOST}" << EOF
sudo apt -y install ansible sshpass
sudo add-apt-repository -y ppa:ansible/ansible
sudo apt -y update
sudo apt -y upgrade ansible
EOF
