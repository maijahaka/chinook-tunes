FROM maven:3.6.3-openjdk-15 AS build-stage

# copy source code from the local directory
COPY . /app
WORKDIR app

# build .jar
RUN mvn package

FROM openjdk:15

COPY --from=build-stage /app/target/chinook-tunes-0.0.1-SNAPSHOT.jar .

ENTRYPOINT ["java", "-jar", "/chinook-tunes-0.0.1-SNAPSHOT.jar"]
EXPOSE 8080