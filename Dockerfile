# syntax=docker/dockerfile:1
FROM ghcr.io/oliviercailloux/diet-server-maven-cache
WORKDIR /app
COPY . .
ENV MAVEN_OPTS="-Djansi.force=true"
ENV MAVEN_CLI_OPTS="-Dstyle.color=always -B"
RUN mvn $MAVEN_CLI_OPTS package
CMD ["java", "-jar", "target/quarkus-app/quarkus-run.jar"]
EXPOSE 10000
