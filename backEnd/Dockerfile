# Use Maven to build the project
FROM maven:3-eclipse-temurin-21 AS builder

# Set the working directory
WORKDIR /app

# Copy the pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the entire project and build it
COPY . .
RUN mvn clean package -DskipTests

# Use Eclipse Temurin for running the application
FROM eclipse-temurin:21-jre-jammy

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file from the builder stage
COPY --from=builder /app/target/contacts-0.0.1-SNAPSHOT.jar /app/contacts.jar

# Expose the Spring Boot server port
EXPOSE 8090

# Run the application
ENTRYPOINT ["java", "-jar", "/app/contacts.jar"]