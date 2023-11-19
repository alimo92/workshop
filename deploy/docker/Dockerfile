FROM amazoncorretto:17

COPY workshop-api/build/libs/workshop-api.jar home/workshop/app.jar
COPY deploy/docker/entrypoint.sh home/workshop/entrypoint.sh

WORKDIR home/workshop

ENTRYPOINT ["sh", "-c", "./entrypoint.sh"]
