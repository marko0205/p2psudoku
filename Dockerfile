FROM alpine/git 
WORKDIR /app 
RUN git clone https://github.com/marko0205/p2psudoku.git 
 
FROM maven:3.5-jdk-8-alpine 
WORKDIR /app 
COPY --from=0 /app/p2psudoku /app 
RUN mvn package -Dmaven.test.skip
 
FROM openjdk:8-jre-alpine 
WORKDIR /app 
ENV MASTERIP=127.0.0.1 
ENV ID=0 
COPY --from=1 /app/target/p2psudoku-1.0-SNAPSHOT-jar-with-dependencies.jar /app 
 
CMD /usr/bin/java -jar p2psudoku-1.0-SNAPSHOT-jar-with-dependencies.jar -m $MASTERIP -id $ID