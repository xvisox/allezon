#!/usr/bin/env bash

# Load the .env file
if [ -f .env ]; then
  . .env
else
  echo ".env file not found."
  exit 1
fi