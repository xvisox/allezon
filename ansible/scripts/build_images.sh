#!/usr/bin/env bash

cd ../../allezon-backend || return
mvn clean install -f pom.xml

# docker login --username username
docker build . -t integraal/allezon-backend
docker push integraal/allezon-backend:latest

# TODO: build aggregator image
