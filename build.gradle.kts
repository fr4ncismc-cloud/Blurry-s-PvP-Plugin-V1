plugins {
    java
    id("io.papermc.paperweight.userdev") version "1.7.1"
}

group = "com.example.bedrockparity"
version = "1.0.0"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

repositories {
    mavenCentral()
    // Floodgate / Geyser artifacts
    maven("https://repo.opencollab.dev/main/")
}

dependencies {
    // Gives us access to internal (Mojang-mapped) server classes - needed for
    // forcing shield-block-start and for reading live attack-cooldown scale.
    paperweight.paperDevBundle("1.21.11-R0.1-SNAPSHOT")

    // -SNAPSHOT always resolves to the newest published build
    compileOnly("org.geysermc.floodgate:api:2.2.3-SNAPSHOT")
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }
    processResources {
        filesMatching("plugin.yml") {
            expand("version" to project.version)
        }
    }
}
