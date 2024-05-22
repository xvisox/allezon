#!/usr/bin/env bash

# Load the .env file
if [ -f .env ]; then
  . .env
  export RTB_MAIN_HOST
  export RTB_KAFKA_HOST
  export RTB_USER
  export RTB_PASSWORD
  export DOCKERHUB_USERNAME
else
  echo ".env file not found."
  exit 1
fi