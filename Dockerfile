# --- Stage 1: Build stage ---
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder
WORKDIR /app

# Copy project files
COPY pom.xml ./
COPY src ./src

# Build application skipping tests
RUN mvn clean package -DskipTests

# --- Stage 2: Runtime stage ---
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Ensure non-interactive execution inside container
ENV SPRING_SHELL_INTERACTIVE_ENABLED=false

# Copy built JAR from builder stage
COPY --from=builder /app/target/funda-makelaar-2026-*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
CMD ["top-realtors"]
