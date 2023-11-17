#!/bin/bash

echo "S3 localstack init"

awslocal s3api create-bucket \
    --bucket users-documents \
    --region us-east-1

awslocal s3api list-buckets
