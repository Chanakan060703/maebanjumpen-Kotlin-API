# Stage 1: Build
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copy Gradle files first for better caching
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle.kts ./
COPY settings.gradle.kts ./

# Make gradlew executable
RUN chmod +x gradlew

# Download dependencies (this layer will be cached if dependencies don't change)
RUN ./gradlew dependencies --no-daemon || true

# Copy source code
COPY src ./src

# Build the application and rename the jar
RUN ./gradlew clean bootJar -x test --no-daemon && \
    find build/libs -name "*.jar" ! -name "*-plain.jar" -exec cp {} app.jar \;

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create directories for uploads and qr_codes
RUN mkdir -p /app/uploads/verify_photos /app/uploads/progression_images /app/qr_codes

# Copy the built jar from build stage
COPY --from=build /app/app.jar app.jar

# Expose port
EXPOSE 8088

# Set JVM options for better container performance
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
