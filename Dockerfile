# Используем официальный OpenJDK образ
FROM openjdk:21-jdk-slim

COPY target/intershop-DEV-SNAPSHOT.jar /app/intershop.jar
COPY uploaded-images /app/uploaded-images

EXPOSE 8080

RUN chmod 777 /app/intershop.jar

ENV IMAGES_DIR=/app/uploaded-images

VOLUME ["/app/uploaded-images"]

# Запускаем приложение
ENTRYPOINT ["/app/intershop.jar"]