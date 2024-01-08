FROM openjdk:17
COPY target/*.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]