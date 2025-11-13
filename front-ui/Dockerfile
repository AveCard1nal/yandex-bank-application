FROM gradle:8.10-jdk-25 AS build
WORKDIR /home/gradle/project
COPY . .
RUN ./gradlew :front-ui:bootJar --no-daemon

FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /home/gradle/project/front-ui/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
