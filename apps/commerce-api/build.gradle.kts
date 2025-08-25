plugins {
    id("org.jetbrains.kotlin.plugin.jpa")
}

dependencies {
    // add-ons
    implementation(project(":modules:jpa"))
    implementation(project(":modules:redis"))
    implementation(project(":supports:jackson"))
    implementation(project(":supports:logging"))
    implementation(project(":supports:monitoring"))

    // web
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:${project.properties["springDocOpenApiVersion"]}")

    // querydsl
    kapt("com.querydsl:querydsl-apt::jakarta")
    implementation ("com.querydsl:querydsl-jpa::jakarta")
    implementation ("com.querydsl:querydsl-core")

    //feign
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")

    implementation("io.github.resilience4j:resilience4j-spring-boot2:2.1.0")
    implementation("com.github.f4b6a3:uuid-creator:5.3.7")

    // test-fixtures
    testImplementation(testFixtures(project(":modules:jpa")))
    testImplementation(testFixtures(project(":modules:redis")))

    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")
}
