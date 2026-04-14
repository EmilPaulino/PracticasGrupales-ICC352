FROM eclipse-temurin:25-jre
WORKDIR /app
COPY build/libs/app2.jar app.jar
EXPOSE 7001
ENTRYPOINT ["java", "-jar", "app.jar"]