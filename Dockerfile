FROM openjdk:17-oracle
WORKDIR /dialogService
COPY target/dialogService-1.0-SNAPSHOT.jar dialog.jar

ENV DB_SERVER_HOST=79.174.84.200
ENV DB_SERVER_PORT=5432
ENV DB_NAME=postgres
ENV DB_SCHEMA=dialog
ENV DB_SERVER_USER=postgres
ENV DB_SERVER_PW=zm443qe

ENV KAFKA_URL=PLAINTEXT://127.0.0.1:9092
#ENV KAFKA_MESSAGE_TOPIC=dialog-topic

ENV EUREKA_URL=http://79.174.84.200:8761/eureka

#ENV FEIGN_URL_AUTHORIZATION=authorization-service
#ENV FEIGN_URL_ACCOUNT=account-service
#ENV FEIGN_URL_FRIEND=friend-service

CMD ["java","-jar","dialog.jar"]
