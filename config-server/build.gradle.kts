plugins {
    id("org.springframework.boot") version "3.5.7"
    id("io.spring.dependency-management")
    java
}

dependencies {
    implementation(platform("org.springframework.boot:spring-boot-dependencies:3.5.7"))

    implementation("org.springframework.cloud:spring-cloud-config-server")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
