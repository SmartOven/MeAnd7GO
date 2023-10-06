FROM gradle:7.5.1-jdk17 AS build

WORKDIR /app
COPY . .
RUN gradle build

FROM openjdk:17-jdk-slim

WORKDIR /app
COPY --from=build /app/build/libs/*.jar /app/client.jar

EXPOSE 8080
CMD ["java", "-jar", "/app/client.jar"]
