# Build stage
# Using Maven 3.9 with JDK 25 to compile Java 24 source code (JDK 25 can compile with --release 24)
FROM maven:3.9.16-eclipse-temurin-25-alpine AS build
WORKDIR /app

# Cache dependency layer:
# pom.xml is copied first because dependencies change far less frequently than source code.
# By copying only pom.xml, Docker can cache the downloaded Maven dependencies.
# The next build will skip downloading dependencies unless pom.xml changes.
COPY pom.xml .
COPY src ./src

# Build the JAR package, skipping tests to accelerate compilation
RUN mvn clean package -DskipTests

# Run stage
# Switch to a lightweight JRE Alpine image to run the compiled JAR file.
FROM eclipse-temurin:24-jre-noble
WORKDIR /app

# Create non-root user for security:
# Running processes as root in a container is a security risk. If a container is compromised,
# the attacker gets root access on the host. Creating a specific 'intiva' group/user and
# running the app under this user adheres to the principle of least privilege.
RUN groupadd -r intiva && useradd -r -g intiva intiva
USER intiva

# Copy the generated JAR from the build stage container
COPY --from=build /app/target/platform-0.0.1-SNAPSHOT.jar app.jar

# Internal port documentation only:
# EXPOSE is metadata. It documents that the container listens on port 8080 internally.
# It DOES NOT publish or make this port publicly accessible on the host machine.
# Nginx handles public traffic on port 80 and forwards requests to this port inside the Docker network.
EXPOSE 8080

# Starts the Spring Boot application.
# -Dspring.profiles.active=prod: Activates the production profile, ensuring production configurations are loaded.
# -Dserver.forward-headers-strategy=framework: Tells Spring to process HTTP proxy headers (X-Forwarded-For, etc.)
#                                              sent by Nginx, so the app correctly identifies client IPs and schemes.
# -jar app.jar: Executes the compiled package.
ENTRYPOINT ["java", \
  "-Dspring.profiles.active=prod", \
  "-Dserver.forward-headers-strategy=framework", \
  "-jar", "app.jar"]
