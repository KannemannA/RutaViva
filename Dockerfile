FROM maven:3.9.0-eclipse-temurin-19-focal AS build
RUN --mount=type=secret,id=privateKey_pem,dst=/etc/secrets/privateKey.pem cp /etc/secrets/privateKey.pem
RUN --mount=type=secret,id=publicKey_pem,dst=/etc/secrets/publicKey.pem cp /etc/secrets/publicKey.pem
COPY . .
RUN mvn clean package -DskipTest

FROM openjdk:22-ea-19-slim-bookworm
COPY --from=build /target/tour-exp-0.1.4-SNAPSHOT.jar rutaViva.jar
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "rutaViva.jar"]
