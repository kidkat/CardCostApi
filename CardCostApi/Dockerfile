FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY target/CardCostApi-1.0.0.jar CardCostApi.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "CardCostApi.jar"]