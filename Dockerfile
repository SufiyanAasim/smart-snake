# Multi-stage Dockerfile for Smart Snake Game
# Build stage
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

# Copy source and configurations
COPY . .

# Compile and package the Java application
RUN mkdir -p out/production/Project && \
    javac -d out/production/Project src/project/*.java && \
    jar --create --file SmartSnakeGame.jar --main-class project.Project -C out/production/Project project

# Run stage (requires an X11 display server to render the Swing GUI)
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the built jar from the builder stage
COPY --from=builder /app/SmartSnakeGame.jar .

# Default command to execute the game
CMD ["java", "-jar", "SmartSnakeGame.jar"]
