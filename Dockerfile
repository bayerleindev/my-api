FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY pom.xml .
RUN mvn --batch-mode dependency:go-offline

COPY src src
RUN mvn --batch-mode clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app

ARG APP_VERSION=blue
ARG APP_COLOR=blue
ENV APP_VERSION=${APP_VERSION}
ENV APP_COLOR=${APP_COLOR}

COPY --from=build --chown=10001:10001 /workspace/target/system-info-api-*.jar app.jar

USER 10001
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
