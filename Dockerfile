FROM amazoncorretto:21

COPY target/*.jar application.jar

ENTRYPOINT ["java", "-Xmx2048M", "-jar", "/application.jar"]