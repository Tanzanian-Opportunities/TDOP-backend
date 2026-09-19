# Stage 1: Build with Maven
FROM eclipse-temurin:21-jdk-jammy AS build

WORKDIR /app

COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 \
    mvn dependency:go-offline -B

COPY src ./src
RUN --mount=type=cache,target=/root/.m2 \
    mvn clean package -DskipTests -B

# Stage 2: Run with JRE
FROM eclipse-temurin:21-jre-jammy AS runtime

RUN groupadd -r tdop && useradd -r -g tdop -d /app tdop

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

RUN chown -R tdop:tdop /app

USER tdop

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -sf http://localhost:8080/api/v1/public/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
