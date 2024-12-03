FROM maven:3.8.1-openjdk-17 AS builder

COPY ./src/ /root/src
COPY ./pom.xml /root/

WORKDIR /root
RUN mvn package
RUN mvn clean install -DskipTests
ADD ./target/UserMicroService-0.0.1-SNAPSHOT.jar /developments/

FROM openjdk:17-jdk-slim-buster
ENV TZ=UTC
ENV DB_IP=ec2-52-48-65-240.eu-west-1.compute.amazonaws.com
ENV DB_PORT=5432
ENV DB_USER=postgres
ENV DB_PASSWORD=cata1609
ENV DB_DBNAME=user_ms

COPY --from=builder /developments/UserMicroService-0.0.1-SNAPSHOT.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
