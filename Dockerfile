FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
ARG xt-marketmaker-0.0.1.jar
ADD target/xt-marketmaker-0.0.1.jar /opt/app.jar

ENTRYPOINT ["java","-jar","/opt/app.jar"]
