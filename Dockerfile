# Etapa 1: compilar con Gradle y Java 21
FROM gradle:8.5.0-jdk21 AS builder

WORKDIR /app
COPY . .

RUN gradle bootJar --no-daemon

FROM eclipse-temurin:21.0.7_6-jre-ubi9-minimal

WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
