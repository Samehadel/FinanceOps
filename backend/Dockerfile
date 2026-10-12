FROM maven:3.9.12-eclipse-temurin-25@sha256:4f82a03a7d6679281952d628131299b1be88d7030a49c6a2b7d2ba2642e44e3e AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -B package -DskipTests
FROM eclipse-temurin:25-jre@sha256:fcd7fd7b387f94bb2ac461478a7436ad8e349924c374ea8313919624dceae636
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
USER 10001
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
