#!/bin/bash

docker-compose --file ./deploy/docker/docker-compose.yaml down

docker-compose --file ./deploy/docker/docker-compose.yaml up --build
