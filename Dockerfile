FROM openjdk:17-jdk-alpine as runtime

COPY /build/libs/technical-challenge-0.0.1-SNAPSHOT.jar /app/app.jar

CMD ["java", "-jar", "/app/app.jar"]
