plugins {
    id("org.springframework.boot") version "3.5.7"
    id("io.spring.dependency-management")
    id("java")
    id("org.springframework.cloud.contract") version "4.3.0"
}

dependencies {
    implementation(platform("org.springframework.boot:spring-boot-dependencies:3.3.5"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.postgresql:postgresql:42.7.3")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("io.micrometer:micrometer-tracing-bridge-brave")
    implementation("io.zipkin.reporter2:zipkin-reporter-brave")
    implementation("net.ttddyy.observation:datasource-micrometer-spring-boot:1.0.3")
    implementation("net.logstash.logback:logstash-logback-encoder:7.4")
    implementation("com.github.danielwegener:logback-kafka-appender:0.2.0")

    implementation(project(":common"))
    implementation("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.cloud:spring-cloud-starter-contract-verifier")
}

contracts {
    testFramework.set(org.springframework.cloud.contract.verifier.config.TestFramework.JUNIT5)
    baseClassForTests.set("ru.yandex.accounts.contract.BaseContractTest")
}