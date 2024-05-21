#!/usr/bin/env bash

PASSWORD="***REMOVED***"
USER="st119"
HOST="st119vm101.rtb-lab.pl"

sshpass -p "${PASSWORD}" ssh ${USER}@${HOST} << EOF
sudo apt -y install ansible sshpass
sudo add-apt-repository -y ppa:ansible/ansible
sudo apt -y update
sudo apt -y upgrade ansible
EOF
