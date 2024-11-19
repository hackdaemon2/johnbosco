# Stage 1: Build the Spring Boot application
FROM maven:latest AS build

# Set the working directory
WORKDIR /app

# Copy the pom.xml and download dependencies
COPY pom.xml /app/
RUN mvn dependency:go-offline

# Copy the rest of the application source code
COPY src /app/src

# Package the application
RUN mvn clean package -DskipTests

# Stage 2: Build the final image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/interswitch.jar /app/interswitch.jar

# Expose the application port
EXPOSE 8080

# Run the JAR file when the container starts
ENTRYPOINT ["java", "-jar", "/app/interswitch.jar"]
