FROM gradle:8.10-jdk-25 AS build
WORKDIR /home/gradle/project
COPY . .
RUN ./gradlew :discovery-server:bootJar --no-daemon

FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /home/gradle/project/discovery-server/build/libs/*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java","-jar","/app/app.jar"]
