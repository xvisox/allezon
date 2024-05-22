#!/usr/bin/env bash

sshpass -p "${RTB_PASSWORD}" ssh "${RTB_USER}"@"${RTB_MAIN_HOST}" << EOF
for i in \$(seq -w 01 10); do
    sshpass -p "${RTB_PASSWORD}" ssh ${RTB_USER}@${RTB_USER}vm1\${i}.rtb-lab.pl -o StrictHostKeyChecking=no -C "/bin/true"
done
EOF
