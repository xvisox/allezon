#!/usr/bin/env bash

PASSWORD="***REMOVED***"
USER="st119"
HOST="st119vm101.rtb-lab.pl"

sshpass -p "${PASSWORD}" ssh ${USER}@${HOST} << EOF
for i in \$(seq -w 01 10); do
    sshpass -p "${PASSWORD}" ssh ${USER}@st119vm1\${i}.rtb-lab.pl -o StrictHostKeyChecking=no -C "/bin/true"
done
EOF
