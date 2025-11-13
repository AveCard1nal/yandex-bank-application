FROM gradle:8.10-jdk-25 AS build
WORKDIR /home/gradle/project
COPY . .
RUN ./gradlew :accounts-service:bootJar --no-daemon

FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /home/gradle/project/accounts-service/build/libs/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/app/app.jar"]
