FROM openjdk:17
ADD build/libs/enrollment.jar .
EXPOSE 80
ENTRYPOINT ["java", "-jar", "enrollment.jar"]