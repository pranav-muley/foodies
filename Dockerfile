# ---------- Stage 1: Build the application ----------
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# ---------- Stage 2: Run the application ----------
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
# Replace this with your actual jar file name
COPY --from=build /app/target/food-ordering-0.0.1-SNAPSHOT.jar app.jar
CMD ["java", "-jar", "app.jar"]
