# MYSQL + DJK17
FROM openjdk:17-alpine
WORKDIR /patient-microservice

COPY build.gradle gradlew settings.gradle ./
COPY gradle/ gradle/
RUN ./gradlew clean build

COPY src/ src/
RUN ./gradlew bootJar
#RUN MYSQL
EXPOSE 8081
CMD ["java", "-jar", "build/libs/patient-microservice.jar"]
