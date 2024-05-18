#!/usr/bin/env bash

scp -r ../aerospike st122@st122vm101.rtb-lab.pl:/

ssh st122@st122vm101.rtb-lab.pl

ansible-playbook --extra-vars "ansible_user=st122 ansible_password=***REMOVED*** ansible_ssh_extra_args='-o StrictHostKeyChecking=no'" -i hosts aerospike.yaml
