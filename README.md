# Requirements

* [Java corretto 17](https://docs.aws.amazon.com/corretto/latest/corretto-17-ug/downloads-list.html)
* [Docker Desktop](https://www.docker.com/products/docker-desktop/)

# Deploy local
There are at least two ways to easily test and deploy the application locally. Start by deploying all the necessary resources for the application to work. e.g. databases like Postgres or DynamoDb
```
./deploy_local.sh
```
Once the necessary resources are up and running, it's possible to run the application using the following command line:

```
./gradlew bootRun
```

it's also possible to run the application by deploying a docker instance locally alongside the necessary resources e.g. databases:
```
./deploy_local --app
```
This is especially useful for testing the application behavior when deploying inside a container which is closer to the prod environment.

# Sonarqube
This repository provides the option to scan the project for code quality. This can be easily done by running the following command:

```
./deploy_sonar.sh
```
This will deploy a sonarqube instance locally, in addition to removing the need for authentication when scanning/creating new projects. One initial scan is done as well. It's possible to check the scan results in http://localhost:9000
It's possible to continue scanning the project by running the following command:

```
./gradlew sonarqube
```

# Swagger
Once the application is deployed locally, it's possible to access all available apis through the following URL: http://localhost:8080/swagger

# Useful command lines
* Delete all docker containers and images

```
docker rm $(docker ps -a -q) -f ; docker rmi $(docker images -q) -f
```
