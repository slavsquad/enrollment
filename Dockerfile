FROM openjdk:17
ADD build/libs/enrollment-0.0.1-SNAPSHOT.jar enrollment.jar
ENTRYPOINT ["java", "-jar","enrollment.jar"]
EXPOSE 80