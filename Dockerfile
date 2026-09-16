# Stage 1: Build with Maven
FROM openjdk:21-jdk-slim AS build

WORKDIR /app

# Install Maven
RUN apt-get update && \
    apt-get install -y maven && \
    rm -rf /var/lib/apt/lists/*

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and compile
COPY src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Run with JRE
FROM openjdk:21-jre-slim AS runtime

WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port 8080
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/api/v1/public/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
