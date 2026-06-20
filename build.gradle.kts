plugins {
    java
}

group = "id.naufal.nexusboosters"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.opencollab.dev/main/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://jitpack.io")
    maven("https://repo.rosewooddev.io/repository/public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    compileOnly("org.geysermc.floodgate:api:2.2.5-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7") {
        exclude(group = "org.bukkit", module = "bukkit")
    }
    compileOnly("org.black_ixx:playerpoints:3.3.4") {
        exclude(group = "org.spigotmc", module = "spigot-api")
        exclude(group = "org.bukkit", module = "bukkit")
    }
    compileOnly("com.github.brcdev-minecraft:shopgui-api:3.2.0") {
        exclude(group = "org.spigotmc", module = "spigot-api")
        exclude(group = "org.bukkit", module = "bukkit")
    }
    implementation("com.zaxxer:HikariCP:5.1.0")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-Xlint:deprecation")
}
