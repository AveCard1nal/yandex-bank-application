plugins {
    id("org.springframework.boot") version "3.3.5"
    id("io.spring.dependency-management")
    id("java")
}

dependencies {
    implementation(platform("org.springframework.boot:spring-boot-dependencies:3.3.5"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    implementation("org.postgresql:postgresql:42.7.3")

    implementation(project(":common"))

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}