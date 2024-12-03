FROM maven:3.8.1-openjdk-17 AS builder

COPY ./src/ /root/src
COPY ./pom.xml /root/

WORKDIR /root
RUN mvn package
RUN mvn clean install -DskipTests
ADD ./target/MCMicroService-0.0.1-SNAPSHOT.jar /developments/
ENTRYPOINT ["java","-jar","/developments/MCMicroService-0.0.1-SNAPSHOT.jar"]
RUN java -Djarmode=layertools -jar /root/target/MCMicroService-0.0.1-SNAPSHOT.jar list
RUN java -Djarmode=layertools -jar /root/target/MCMicroService-0.0.1-SNAPSHOT.jar extract
RUN ls -l /root

FROM openjdk:17-jdk-slim-buster

ENV TZ=UTC
ENV DB_IP=ec2-52-48-65-240.eu-west-1.compute.amazonaws.com
ENV DB_PORT=5432
ENV DB_USER=postgres
ENV DB_PASSWORD=cata1609
ENV DB_DBNAME=measure_ms


COPY --from=builder /root/dependencies/ ./
COPY --from=builder /root/snapshot-dependencies/ ./

RUN sleep 10
COPY --from=builder /root/spring-boot-loader/ ./
COPY --from=builder /root/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher","-XX:+UseContainerSupport -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -XX:MaxRAMFraction=1 -Xms512m -Xmx512m -XX:+UseG1GC -XX:+UseSerialGC -Xss512k -XX:MaxRAM=72m"]