plugins {
    id("org.springframework.boot") version "3.5.7"
    id("io.spring.dependency-management")
    java
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.security:spring-security-oauth2-authorization-server:1.3.1")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}