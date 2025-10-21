# Multi-stage Dockerfile for a Spring Boot application (Maven or mvnw)
# Place this file at the project root (same level as pom.xml or mvnw)

# -------- build stage --------
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /workspace

# Copy sources
COPY . .

# Ensure mvnw is executable if present, then build (use mvnw if available, otherwise system mvn)
RUN if [ -f ./mvnw ]; then chmod +x ./mvnw && ./mvnw -DskipTests package -DskipTests; else mvn -DskipTests package -DskipTests; fi

# -------- runtime stage --------
FROM eclipse-temurin:17-jre
ARG JAR_FILE=target/*.jar
WORKDIR /app

# Copy the built Spring Boot jar from the builder stage
COPY --from=builder /workspace/${JAR_FILE} app.jar

# (Optional) create non-root user
RUN addgroup --system spring && adduser --system --ingroup spring spring
USER spring:spring

EXPOSE 8080
ENV JAVA_OPTS=""
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]