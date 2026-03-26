# ── Etapa 1: Build ──────────────────────────────────────────
FROM gradle:9.2.0-jdk21 AS build
WORKDIR /app

COPY build.gradle settings.gradle* ./
RUN gradle dependencies --no-daemon || true

COPY src ./src
RUN gradle shadowJar --no-daemon

# ── Etapa 2: Runtime ────────────────────────────────────────
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

COPY --from=build /app/build/libs/app.jar app.jar

RUN mkdir -p /app/data

EXPOSE 7070

ENTRYPOINT ["java", "-jar", "app.jar"]