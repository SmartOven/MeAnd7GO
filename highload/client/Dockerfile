FROM gradle:7.5.1-jdk17 AS build

WORKDIR /app
COPY . .
ENV HIGHLOAD_SERVICE_IP localhost
ENV HIGHLOAD_SERVICE_PORT 8080
RUN gradle build

FROM openjdk:17-jdk-slim

WORKDIR /app
COPY --from=build /app/build/libs/*.jar /app/client.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/client.jar"]
