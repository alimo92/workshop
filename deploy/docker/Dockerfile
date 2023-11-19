FROM amazoncorretto:17

COPY workshop-api/build/libs/workshop-api.jar home/workshop/app.jar

WORKDIR home/workshop

ENTRYPOINT ["sh", "-c", "java -jar app.jar"]
