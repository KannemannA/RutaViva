FROM maven:3.9.0-eclipse-temurin-19-focal AS build
COPY . .
RUN mvn clean package -DskipTest

FROM openjdk:22-ea-19-slim-bookworm
COPY --from=build /target/tour-exp-0.1.4-SNAPSHOT.jar rutaViva.jar
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "rutaViva.jar"]
