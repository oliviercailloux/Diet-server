# syntax=docker/dockerfile:1
FROM debian:bullseye-slim
ENV DEBIAN_FRONTEND="noninteractive"
RUN apt-get update && apt-get -qy --no-install-recommends install openjdk-17-jdk-headless maven
ENV LANG="C.UTF-8"
WORKDIR /app
COPY . .
RUN mvn package
RUN mvn org.apache.maven.plugins:maven-dependency-plugin:3.3.0:copy-dependencies
CMD ["java", "-jar", "target/quarkus-app/quarkus-run.jar"]
EXPOSE 80
