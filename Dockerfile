# ==========================================
# BUILDER
# ==========================================
FROM gradle:8.14-jdk21 AS builder
WORKDIR /app
COPY . .
ARG SERVICE_NAME
RUN gradle :${SERVICE_NAME}:bootJar -x test --no-daemon

# ==========================================
# RUNNER
# ==========================================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
ARG SERVICE_NAME
COPY --from=builder /app/${SERVICE_NAME}/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]