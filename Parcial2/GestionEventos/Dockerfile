# ---------- Stage 1: Build ----------
FROM gradle:9.2-jdk21 AS build

WORKDIR /app

COPY . .

RUN gradle shadowJar

# ---------- Stage 2: Run ----------
FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY --from=build /app/build/libs/*-all.jar app.jar

EXPOSE 7000

CMD ["java","-jar","app.jar"]