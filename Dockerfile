FROM eclipse-temurin:21-jdk AS builder

# Set working directory
WORKDIR /app

# Setup MICRONAUT_VERSION env to avoid errors
ENV MICRONAUT_VERSION=4.7.4

# Setup DOCKER_BUILD env to skip spotless check
ENV DOCKER_BUILD=true

# Copy Gradle files first for better caching
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY gradle.properties .
COPY openapi.properties .

# Give execute permission to gradlew
RUN chmod +x gradlew

# Copy source code
COPY src src

# Build the application
RUN ./gradlew shadowJar -x test

# Stage 2: Create the runtime image
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the built artifact from builder stage
COPY --from=builder /app/build/libs/*-all.jar app.jar

# Expose the port your app runs on
EXPOSE 8080

# Set the command to run your application
ENTRYPOINT ["java", "-jar", "app.jar"]