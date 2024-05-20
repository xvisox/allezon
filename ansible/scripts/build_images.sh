#!/usr/bin/env bash

DOCKERHUB_USERNAME="integraal"

# Docker login (optional, uncomment if needed)
# docker login --username "$DOCKERHUB_USERNAME"

cd ../../allezon-backend || exit
mvn clean install -f pom.xml
docker build . -t "$DOCKERHUB_USERNAME/allezon-backend"
docker push "$DOCKERHUB_USERNAME/allezon-backend:latest"

cd ../allezon-aggregator || exit
mvn clean install -f pom.xml
docker build . -t "$DOCKERHUB_USERNAME/allezon-aggregator"
docker push "$DOCKERHUB_USERNAME/allezon-aggregator:latest"
