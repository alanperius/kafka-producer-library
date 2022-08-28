FROM openjdk

WORKDIR /app

COPY build/libs/library-events-producer-0.0.1-SNAPSHOT.jar /app/kafka-producer-app.jar

ENTRYPOINT ["java", "-jar", "kafka-producer-app.jar"]