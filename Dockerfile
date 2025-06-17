# Dockerfile (Final Version for Root Directory)
FROM openjdk:21-jdk-slim AS builder
WORKDIR /app
COPY . .
RUN mkdir out
RUN javac -d out src/game/*.java src/players/*.java src/Main.java
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY index.html ./
COPY game.js ./
COPY --from=builder /app/out/ .
EXPOSE 8080
CMD ["java", "-cp", ".", "Main"]