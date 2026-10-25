# Multi-stage Dockerfile for the Spring Boot Service Registry
# Place this file at: backend/service-registry/Dockerfile

FROM maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY pom.xml ./pom.xml
RUN mvn -B -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -B -DskipTests package

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /workspace/target/*.jar /app/app.jar

EXPOSE 8761

# Compose supplies SERVER_PORT=8761.
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/app.jar"]
