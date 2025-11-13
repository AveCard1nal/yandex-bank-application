FROM gradle:8.10-jdk-25 AS build
WORKDIR /home/gradle/project
COPY . .
RUN ./gradlew :gateway:bootJar --no-daemon

FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /home/gradle/project/gateway/build/libs/*.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java","-jar","/app/app.jar"]
