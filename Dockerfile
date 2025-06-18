# Dockerfile (Final Version for Root Directory)

# Step 1: Compilation Stage 'builder'
FROM openjdk:21-jdk-slim AS builder
WORKDIR /app
COPY . .
RUN mkdir out
RUN javac -d out src/game/*.java src/players/*.java src/Main.java

# Step 2: Final Runtime Stage
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY index.html ./
COPY game.js ./
COPY images/ ./images/
COPY --from=builder /app/out/ .
EXPOSE 8080
CMD ["java", "-cp", ".", "Main"]