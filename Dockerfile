FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/your-artifact.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
