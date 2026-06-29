# Build phase
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY reporting-service ./reporting-service
WORKDIR /app/reporting-service
RUN mvn clean package -DskipTests

# Run phase
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/reporting-service/target/*.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "app.jar"]
