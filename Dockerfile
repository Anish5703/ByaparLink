
# Step 1: Build
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy only what's needed
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests


# Step 2: Run
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/ByaparLink-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
