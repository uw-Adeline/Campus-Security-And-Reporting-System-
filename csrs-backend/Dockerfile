# ============================================================
# STAGE 1: BUILD
# Use Maven + Java 17 to compile and package the application
# ============================================================
FROM maven:3.9.6-eclipse-temurin-17 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven project file first (for dependency caching)
# Docker caches this layer — if pom.xml hasn't changed, it won't
# re-download all dependencies on every build (saves time)
COPY pom.xml .

# Download all dependencies (cached layer)
RUN mvn dependency:go-offline -B

# Copy the full source code
COPY src ./src

# Build the application, skipping tests (tests run in Phase 4)
RUN mvn clean package -DskipTests

# ============================================================
# STAGE 2: RUN
# Use a lightweight Java 17 runtime image (no Maven needed)
# This keeps the final image small (~200MB vs ~600MB)
# ============================================================
FROM eclipse-temurin:17-jre-alpine

# Set working directory
WORKDIR /app

# Copy only the compiled JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port 8080 (the port Spring Boot runs on)
EXPOSE 8080

# Command to run the application when the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]
