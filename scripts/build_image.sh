#!/usr/bin/env bash

cd ../
mvn clean install -f pom.xml

# docker login --username username
docker build . -t integraal/allezon
docker push integraal/allezon:latest
