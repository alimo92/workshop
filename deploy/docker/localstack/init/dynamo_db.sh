#!/bin/bash

echo "DynamoDb localstack init"

awslocal dynamodb create-table \
   --table-name purchases \
   --attribute-definitions 'file://bin/dynamo_db/purchases_attributes.json' \
   --key-schema 'file://bin/dynamo_db/purchases_key_schema.json' \
   --region 'us-east-1' \
   --billing-mode 'PROVISIONED' \
   --provisioned-throughput 'file://bin/dynamo_db/purchases_capacities.json' \
   --tags 'file://bin/aws_tags.json'
