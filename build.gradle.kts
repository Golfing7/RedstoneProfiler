plugins {
    id("java")
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.17"
}

group = "com.golfing8"
version = "1.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://maven.enginehub.org/repo/")
}

dependencies {
    // For annotations
    annotationProcessor("org.projectlombok:lombok:1.18.42")
    compileOnly("org.projectlombok:lombok:1.18.42")

    // For pasting schematics
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.3.16")

    // The paper server API
    compileOnly("io.papermc.paper:paper-api:1.21.5-R0.1-SNAPSHOT")
    paperweight.paperDevBundle("1.21.5-R0.1-SNAPSHOT")
}