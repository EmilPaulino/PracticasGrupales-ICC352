FROM eclipse-temurin:25-jre
WORKDIR /app
COPY build/libs/app.jar app.jar
EXPOSE 7000
EXPOSE 50051
ENTRYPOINT ["java", "-jar", "app.jar"]