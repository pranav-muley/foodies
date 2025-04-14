# Use OpenJDK base image
FROM openjdk:11-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file from the target directory to the container
COPY target/food-ordering-0.0.1-SNAPSHOT.jar /app/app.jar

# Expose the port your app will run on (in this case, 9091)
EXPOSE 9091

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
