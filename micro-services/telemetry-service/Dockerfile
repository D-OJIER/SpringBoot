# Build phase
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY telemetry-service ./telemetry-service
WORKDIR /app/telemetry-service
RUN mvn clean package -DskipTests

# Run phase
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/telemetry-service/target/*.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]
