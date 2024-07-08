FROM eclipse-temurin:21
EXPOSE 9080
RUN mkdir /opt/app
COPY ./target/money-transfer*.jar /opt/app/money-transfer.jar
CMD ["java", "-jar", "/opt/app/money-transfer.jar"]
