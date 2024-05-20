#!/usr/bin/env bash

PASSWORD="***REMOVED***"
USER="st122"
HOST="st122vm101.rtb-lab.pl"

sshpass -p "${PASSWORD}" ssh ${USER}@${HOST} << EOF
for i in `seq -w 01 10`; do sshpass -p ***REMOVED*** ssh st122@st122vm1$i.rtb-lab.pl -o StrictHostKeyChecking=no -C "/bin/true"; done
EOF