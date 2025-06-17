# Dockerfile (Final Version)

# Step 1: Compilation Stage 'builder'
# Use a full, reliable JDK image. This ensures javac works correctly.
FROM openjdk:21-jdk-slim AS builder

# Set the working directory for the build process.
WORKDIR /app

# Copy all your project files (including the 'src' directory).
COPY . .

# Create an 'out' directory for the compiled class files.
RUN mkdir out

# Compile all .java files from the 'src' directory and put them in the 'out' directory.
# This command is robust and handles your package structure.
RUN javac -d out src/game/*.java src/players/*.java src/Main.java


# Step 2: Final Runtime Stage
# Use the SAME full JDK image. This is the simplest and most reliable way
# to guarantee that the 'java' command is available and works correctly.
FROM openjdk:21-jdk-slim

# Set the working directory for the final running container.
WORKDIR /app

# Copy your frontend files from the original project context.
COPY index.html ./
COPY game.js ./

# Copy ONLY the compiled application files from the 'builder' stage's 'out' directory.
# This keeps the final image clean, without source code or build tools.
COPY --from=builder /app/out/ .

# Tell Render that the application inside the container will use port 8080.
EXPOSE 8080

# The command to execute when the container starts.
# -cp . sets the classpath to the current directory (/app).
# 'Main' is the name of your main class.
CMD ["java", "-cp", ".", "Main"]