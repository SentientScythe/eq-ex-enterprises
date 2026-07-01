pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.kikugie.dev/releases")
        maven("https://maven.architectury.dev/")
        maven("https://maven.fabricmc.net/")
        maven("https://maven.minecraftforge.net/")
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.9.6"
}

rootProject.name = "ProjectE-Stonecutter"

stonecutter {
    create(rootProject) {
        versions("1.10.2", "1.11.2", "1.12.2", "1.13", "1.14.4", "1.15.2", "1.16.5", "1.18.2", "1.19.2", "1.20.1", "1.20.4", "1.21.1")
    }
}
