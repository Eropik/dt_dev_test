plugins {
    id("application")
    id("java")
}

group = "com.vizor.test"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

application {
    mainClass.set("com.vizor.test.Main")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(11)
    }
}














