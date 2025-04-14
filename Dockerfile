# Use the official OpenJDK image as a base
FROM openjdk:11-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file to the container
COPY target/food-ordering-0.0.1-SNAPSHOT.jar /app/app.jar

# Expose the port your app will run on
EXPOSE 8080

# Command to run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
