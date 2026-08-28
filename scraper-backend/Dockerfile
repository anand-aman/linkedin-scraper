FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY pom.xml ./pom.xml
COPY scrapper-backend/pom.xml ./scrapper-backend/pom.xml
COPY scrapper-backend/src ./scrapper-backend/src

RUN mvn -f scrapper-backend/pom.xml -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /workspace/scrapper-backend/target/*.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
