FROM gradle:7.5.1-jdk17 AS build

WORKDIR /app
COPY . .
RUN gradle build

FROM openjdk:17-jdk-slim

WORKDIR /app
COPY --from=build /app/build/libs/*.jar /app/sharding-proxy-service.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/sharding-proxy-service.jar"]