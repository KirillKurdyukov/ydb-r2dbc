plugins {
    id("java")
}

group = "tech.ydb.io.r2dbc.spi"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.r2dbc:r2dbc-spi:0.9.1.RELEASE")
    implementation("tech.ydb:ydb-sdk-table:2.1.4") {
        exclude("javax.annotation", "javax.annotation-api")
    }
    implementation("io.projectreactor:reactor-core:3.5.9")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.test {
    useJUnitPlatform()
}