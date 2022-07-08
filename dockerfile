FROM openjdk:16-jdk

ARG JAR_FILE=build/libs/*-SNAPSHOT.jar

COPY ${JAR_FILE} smrp.jar
COPY medicine.csv medicine.csv
ENTRYPOINT ["java", "-Dspring.profiles.active=dev", "-jar", "/smrp.jar"]
