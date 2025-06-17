# Step 1: Use an official OpenJDK image as a base for compiling the code.
# We use a specific version for consistency. '21-jdk-slim' is a good, lightweight choice.
FROM openjdk:21-jdk-slim AS builder

# Set the working directory inside the container.
WORKDIR /app

# Copy all the project files from your computer into the container's /app directory.
COPY . .

# Run the Java compiler to compile all .java files.
# The output .class files will be placed in their respective package directories.
RUN javac -d . game/*.java players/*.java Main.java


# Step 2: Now, create the final, smaller image for running the application.
# We use a JRE (Java Runtime Environment) image because it's smaller than a JDK.
FROM openjdk:21-jre-slim

# Set the working directory again for the final image.
WORKDIR /app

# Copy ONLY the compiled .class files and the necessary web files from the 'builder' stage.
# This makes our final container much smaller and more secure.
COPY --from=builder /app/game/ ./game/
COPY --from=builder /app/players/ ./players/
COPY --from=builder /app/Main*.class ./
COPY --from=builder /app/index.html ./
COPY --from=builder /app/game.js ./

# Expose port 8080. This tells Docker that our application will listen on this port inside the container.
# Render will automatically detect this.
EXPOSE 8080

# The command to run when the container starts.
# This is the equivalent of you typing 'java Main' in your terminal.
CMD ["java", "Main"]