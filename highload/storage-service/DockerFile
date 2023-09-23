# Используем базовый образ с Java 17
FROM openjdk:17-jre-slim

# Создаем рабочий каталог внутри контейнера
WORKDIR /app

# Копируем JAR-файл в контейнер (предполагается, что JAR-файл находится в директории build/libs)
COPY build/libs/service-1.0.jar /app/service.jar

# Запускаем Java приложение
CMD ["java", "-jar", "service.jar"]
