# Multi-stage build for RaceHub Backend

# Stage 1: Build with Maven
FROM maven:3.8.5-openjdk-17 AS build

WORKDIR /app

# Copy pom.xml and download dependencies (cached layer)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime with Eclipse Temurin (OpenJDK successor)
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Install curl for health check
RUN apk add --no-cache curl

# Copy JAR from build stage
COPY --from=build /app/target/racehub-backend-0.0.1-SNAPSHOT.jar app.jar

# Expose port (Render will override with PORT env var)
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:${PORT:-8080}/actuator/health || exit 1

# Run the application with dynamic port binding
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-8080} -Xmx512m -Xms256m -jar app.jar"]