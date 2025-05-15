FROM maven:3.9-eclipse-temurin-21 as builder

WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests



FROM openjdk:21-jdk-slim

COPY --from=builder /app/target/*-SNAPSHOT.jar /app/intershop.jar
COPY uploaded-images /app/uploaded-images

EXPOSE 8080

RUN chmod 777 /app/intershop.jar

ENV IMAGES_DIR=/app/uploaded-images

VOLUME ["/app/uploaded-images"]

ENTRYPOINT ["/app/intershop.jar"]