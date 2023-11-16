#!/bin/bash

docker-compose --file ./docker-compose-sonarqube.yaml up --build -d

echo Waiting for sonarqube availability...
sleep 30

# Add provisioning permissions to anyone
curl -X POST -v -u admin:admin 'http://localhost:9000/api/permissions/add_group?groupName=anyone&permission=provisioning'

# Add scan permissions to anyone
curl -X POST -v -u admin:admin 'http://localhost:9000/api/permissions/add_group?groupName=anyone&permission=scan'
