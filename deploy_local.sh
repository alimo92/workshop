#!/bin/bash

./gradlew spotlessApply build

if [ $# -eq 1 ] && [ "$1" = "--app" ]
then
  docker-compose --profile app --file ./deploy/docker/docker-compose.yaml up --build
else
  docker-compose --file ./deploy/docker/docker-compose.yaml up --build
fi
