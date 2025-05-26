FROM maven:3.9-eclipse-temurin-21 as builder

WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests


FROM openjdk:21-jdk-slim

COPY --from=builder /app/client-service/target/*-SNAPSHOT.jar /app/client-service.jar
COPY ./client-service/uploaded-images /app/uploaded-images

EXPOSE 8080

RUN chmod 777 /app/client-service.jar

ENV IMAGES_DIR=/app/uploaded-images

VOLUME ["/app/uploaded-images"]

ENTRYPOINT ["/app/client-service.jar"]