#!/bin/bash

echo "SecretManager localstack init"

awslocal secretsmanager create-secret \
   --name credentials \
   --description 'User credentials' \
   --secret-string 'file://bin/secrets.json'
