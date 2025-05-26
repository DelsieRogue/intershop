FROM maven:3.9-eclipse-temurin-21 as builder

WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests


FROM openjdk:21-jdk-slim

COPY --from=builder /app/payment-service/target/*-SNAPSHOT.jar /app/payment-service.jar

EXPOSE 8082

RUN chmod 777 /app/payment-service.jar

ENTRYPOINT ["/app/payment-service.jar"]