FROM openjdk:17
WORKDIR /app
COPY target/allezon.jar /app/allezon.jar
ENTRYPOINT ["java", "-jar", "allezon.jar"]
