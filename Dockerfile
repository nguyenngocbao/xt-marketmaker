# ---- build: đóng gói jar bằng Maven ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /src
COPY pom.xml ./
RUN mvn -B -q dependency:go-offline
COPY src ./src
RUN mvn -B -q package -DskipTests && cp target/*.jar /src/app.jar

# ---- runtime: chỉ JRE ----
FROM eclipse-temurin:17-jre
WORKDIR /opt
COPY --from=build /src/app.jar /opt/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/opt/app.jar"]
