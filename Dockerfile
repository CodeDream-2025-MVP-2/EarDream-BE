# EarDream MVP Dockerfile
# Multi-stage build for production optimization

# Stage 1: Build stage
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copy gradle wrapper and build files
COPY gradle/ gradle/
COPY gradlew build.gradle settings.gradle ./

# Download dependencies (cached layer)
RUN ./gradlew dependencies --no-daemon

# Copy source code
COPY src/ src/

# Build application
RUN ./gradlew build -x test --no-daemon

# Stage 2: Runtime stage
FROM eclipse-temurin:21-jre-alpine

# Install curl for health checks
RUN apk add --no-cache curl

# Create app user for security
RUN addgroup -g 1000 eardream && \
    adduser -u 1000 -G eardream -s /bin/sh -D eardream

# Create app directory
WORKDIR /app

# Copy JAR from build stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Copy Oracle wallet (if needed)
COPY src/main/resources/wallet/ wallet/

# Set ownership
RUN chown -R eardream:eardream /app

# Switch to non-root user
USER eardream

# Expose port
EXPOSE 8080

# Environment variables
ENV JAVA_OPTS="-Xms512m -Xmx1024m -Dfile.encoding=UTF-8" \
    SPRING_PROFILES_ACTIVE=prod \
    ORACLE_WALLET_LOCATION=/app/wallet \
    ORACLE_TNS_ADMIN=/app/wallet

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]