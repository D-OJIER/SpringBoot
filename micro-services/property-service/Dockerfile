# Build phase
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY property-service ./property-service
RUN mvn clean package -pl property-service -DskipTests

# Run phase
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/property-service/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
